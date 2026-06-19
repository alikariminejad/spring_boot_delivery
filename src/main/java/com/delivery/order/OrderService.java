package com.delivery.order;

import com.delivery.dto.CreateOrderRequest;
import com.delivery.dto.OrderResponse;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;


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

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderId(savedOrder.getId());
        orderStatusHistory.setFromStatus(null);
        orderStatusHistory.setToStatus(OrderStatus.PENDING);
        orderStatusHistory.setChangedByUserId(user.getId());
        orderStatusHistory.setNote("Order placed");
        orderStatusHistoryRepository.save(orderStatusHistory);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus().name(),
                savedOrder.getPrice(),
                savedOrder.getOrigin(),
                savedOrder.getDestination(),
                savedOrder.getWeight(),
                savedOrder.getDimensions(),
                savedOrder.getDescription(),
                savedOrder.getCreatedAt());
    }
}
