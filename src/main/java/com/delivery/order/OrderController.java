package com.delivery.order;

import com.delivery.dto.CreateOrderRequest;
import com.delivery.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                                    @Valid @RequestBody CreateOrderRequest request){
        OrderResponse response = orderService.createOrder(request, userDetails.getUsername());
        return new ResponseEntity<OrderResponse>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC)Pageable pageable
            ){
        Page<OrderResponse> orders = orderService.getCustomerOrders(userDetails.getUsername(), status, pageable);
        return new ResponseEntity<Page<OrderResponse>>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> getOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable UUID orderId){
        String username = userDetails.getUsername();
        OrderResponse order = orderService.getOrderById(orderId, username);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable UUID orderId){
        String username = userDetails.getUsername();
        OrderResponse order = orderService.cancelOrder(orderId, username);
        return ResponseEntity.ok(order);
    }

}
