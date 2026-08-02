package com.delivery.order;

import com.delivery.dto.CreateOrderRequest;
import com.delivery.dto.OrderResponse;
import com.delivery.mapper.OrderMapper;
import com.delivery.notification.NotificationService;
import com.delivery.notification.NotificationType;
import com.delivery.user.Role;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import com.delivery.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final WalletService walletService;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;


    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username doesn't exist :/"));
        Order order = new Order();
        order.setCustomer(user);
        order.setOrigin(request.getOrigin());
        order.setDestination(request.getDestination());

        order.setWeight(request.getWeight());
        order.setDimensions(request.getDimensions());
        order.setDescription(request.getDescription());

        BigDecimal price = BigDecimal.valueOf(5.0)
                .add(BigDecimal.valueOf(request.getWeight())
                        .multiply(BigDecimal.valueOf(2.5)));
        order.setPrice(price);
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        UUID savedOrderId = savedOrder.getId();
        walletService.processPayment(user, price, savedOrderId, "Payment for order");


        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderId(savedOrderId);
        orderStatusHistory.setFromStatus(null);
        orderStatusHistory.setToStatus(OrderStatus.PENDING);
        orderStatusHistory.setChangedByUserId(user.getId());
        orderStatusHistory.setNote("Order placed");
        orderStatusHistoryRepository.save(orderStatusHistory);

        String notifMessage = "Order with id:" + savedOrderId + " is created.";
        rabbitTemplate.
        notificationService.createNotification(username, notifMessage, NotificationType.ORDER_PLACED, savedOrderId);

        return orderMapper.toDto(savedOrder);
    }

    public Page<OrderResponse> getCustomerOrders(String username, OrderStatus statusFilter, Pageable pageable){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Username doesn't exist :/"));
        Page<Order> orders = (statusFilter != null)
                ? orderRepository.findByCustomerAndStatus(user, statusFilter, pageable)
                : orderRepository.findByCustomer(user, pageable);
        return orders.map(order->orderMapper.toDto(order));
    }

    public OrderResponse getOrderById(UUID orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with this id: " + orderId + " not found"));

        if(!order.getCustomer().getUsername().equals(username)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to access this order");
        }

        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with this id: " + orderId + " not found"));

        if(!order.getCustomer().getUsername().equals(username)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to cancel this order");
        }

        if(order.getStatus() != OrderStatus.PENDING){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order can only be cancelled when PENDING");
        }

        order.setStatus(OrderStatus.CANCELED);
        Order cancelledOrder = orderRepository.save(order);

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderId(orderId);
        orderStatusHistory.setFromStatus(OrderStatus.PENDING);
        orderStatusHistory.setToStatus(OrderStatus.CANCELED);
        orderStatusHistory.setChangedByUserId(order.getCustomer().getId());
        orderStatusHistory.setNote("Order canceled by user");
        orderStatusHistoryRepository.save(orderStatusHistory);

        return orderMapper.toDto(cancelledOrder);
    }

    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_TRANSITIONS =
            Map.of(
                    OrderStatus.PENDING, List.of(OrderStatus.ASSIGNED, OrderStatus.CANCELED),
                    OrderStatus.ASSIGNED, List.of(OrderStatus.CONFIRMED, OrderStatus.PENDING),
                    OrderStatus.CONFIRMED, List.of(OrderStatus.PICKED_UP),
                    OrderStatus.PICKED_UP, List.of(OrderStatus.IN_TRANSIT),
                    OrderStatus.IN_TRANSIT, List.of(OrderStatus.DELIVERED)

    );

    public void validateTransition(Order order, OrderStatus newStatus) {
        Boolean isValid = ALLOWED_TRANSITIONS.getOrDefault(order.getStatus(), List.of())
                .contains(newStatus);
        if(!isValid){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status transition");
        }
    }

    public Page<OrderResponse> getAllOrders(OrderStatus statusFilter, Pageable pageable){
        Page<Order> orders = (statusFilter != null) ? orderRepository.findByStatus(statusFilter, pageable)
                : orderRepository.findAll(pageable);
        return orders.map((order)->orderMapper.toDto(order));
    }

    @Transactional
    public OrderResponse assignOrder(UUID orderId, String courierName, String performedByUsername){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with this id: "+orderId+" not found"));
        if(order.getStatus() !=OrderStatus.PENDING){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending order can be assigned");
        }
        User courier = userRepository.findByUsername(courierName)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier was not found"));
        if(courier.getRole() != Role.COURIER){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only couriers can be assigned to orders");
        }
        order.setCourier(courier);
        order.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderId(orderId);
        orderStatusHistory.setFromStatus(OrderStatus.PENDING);
        orderStatusHistory.setToStatus(OrderStatus.ASSIGNED);
        User admin = userRepository.findByUsername(performedByUsername)
                        .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with this name: "+ performedByUsername));
        orderStatusHistory.setChangedByUserId(admin.getId());
        orderStatusHistoryRepository.save(orderStatusHistory);

        String notifMessage = "Order with id:" + orderId + " is just assigned to you.";
        notificationService.createNotification(courierName, notifMessage, NotificationType.COURIER_ASSIGNED, orderId);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponse acceptOrder(UUID orderId, String courierUsername){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with this id: " + orderId));
        if(!order.getCourier().getUsername().equals(courierUsername)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order's courier is not this user: " + courierUsername);
        }
        if(order.getStatus() != OrderStatus.ASSIGNED){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is not assigned");
        }
        validateTransition(order, OrderStatus.CONFIRMED);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderId(orderId);
        orderStatusHistory.setFromStatus(OrderStatus.ASSIGNED);
        orderStatusHistory.setToStatus(OrderStatus.CONFIRMED);
        User courier = userRepository.findByUsername(courierUsername)
                        .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier was not found"));
        orderStatusHistory.setChangedByUserId(courier.getId());
        orderStatusHistoryRepository.save(orderStatusHistory);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponse rejectOrder(UUID orderId, String courierUsername){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with this id: " + orderId));

        if(!order.getCourier().getUsername().equals(courierUsername)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order's courier is not this user: " + courierUsername);
        }
        if(order.getStatus() != OrderStatus.ASSIGNED){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is not 'assigned'");
        }

        validateTransition(order, OrderStatus.PENDING);
        order.setStatus(OrderStatus.PENDING);
        order.setCourier(null);
        orderRepository.save(order);

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderId(orderId);
        orderStatusHistory.setFromStatus(OrderStatus.ASSIGNED);
        orderStatusHistory.setToStatus(OrderStatus.PENDING);
        User courier = userRepository.findByUsername(courierUsername)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier was not found"));
        orderStatusHistory.setChangedByUserId(courier.getId());
        orderStatusHistoryRepository.save(orderStatusHistory);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus, String courierUsername){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with this id: " + orderId + " was not found"));
        if(!order.getCourier().getUsername().equals(courierUsername)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order's courier name is not "+ courierUsername);
        }
        OrderStatus oldStatus = order.getStatus();
        validateTransition(order, newStatus);
        order.setStatus(newStatus);

        if(newStatus== OrderStatus.DELIVERED){
            BigDecimal commission = order.getPrice().multiply(BigDecimal.valueOf(0.8));
            walletService.creditCourier(order.getCourier(), commission, orderId);
        }

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderId(orderId);
        orderStatusHistory.setFromStatus(oldStatus);
        orderStatusHistory.setToStatus(newStatus);
        User courier = userRepository.findByUsername(courierUsername)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier with this name: " + courierUsername +" was not found"));
        orderStatusHistory.setChangedByUserId(courier.getId());
        orderStatusHistoryRepository.save(orderStatusHistory);

        String notifMessage = "Order with id:" + orderId + "'s status change from:" + oldStatus.toString() +" to: " + newStatus.toString();
        notificationService.createNotification(order.getCustomer().getUsername(), notifMessage, NotificationType.STATUS_CHANGED, orderId);
        notificationService.createNotification(courierUsername, notifMessage, NotificationType.STATUS_CHANGED, orderId);
        return orderMapper.toDto(order);
    }

    public Page<OrderResponse> getCourierOrders(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Page<Order> orders=  orderRepository.findByCourier(user, pageable);
        return orders.map(orderMapper::toDto);
    }
}
