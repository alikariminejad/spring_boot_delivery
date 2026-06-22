package com.delivery.order;

import com.delivery.dto.CreateOrderRequest;
import com.delivery.dto.OrderResponse;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import com.delivery.wallet.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final WalletService walletService;


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

        walletService.processPayment(user, price, savedOrder.getId(), "Payment for order");


        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderId(savedOrder.getId());
        orderStatusHistory.setFromStatus(null);
        orderStatusHistory.setToStatus(OrderStatus.PENDING);
        orderStatusHistory.setChangedByUserId(user.getId());
        orderStatusHistory.setNote("Order placed");
        orderStatusHistoryRepository.save(orderStatusHistory);

        return mapToResponse(savedOrder);
    }

    public Page<OrderResponse> getCustomerOrders(String username, OrderStatus statusFilter, Pageable pageable){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Username doesn't exist :/"));
        Page<Order> orders = (statusFilter != null)
                ? orderRepository.findByCustomerAndStatus(user, statusFilter, pageable)
                : orderRepository.findByCustomer(user, pageable);
        return orders.map(order -> mapToResponse(order));
    }

    public OrderResponse getOrderById(UUID orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with this id: " + orderId + " not found"));

        if(!order.getCustomer().getUsername().equals(username)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to access this order");
        }

        return mapToResponse(order);
    }

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

        return mapToResponse(cancelledOrder);
    }

    private OrderResponse mapToResponse(Order order){
        return new OrderResponse(
                order.getId(),
                order.getStatus().toString(),
                order.getPrice(),
                order.getOrigin(),
                order.getDestination(),
                order.getWeight(),
                order.getDimensions(),
                order.getDescription(),
                order.getCreatedAt()
        );
    }
}
