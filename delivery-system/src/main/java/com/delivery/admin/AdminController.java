package com.delivery.admin;

import com.delivery.dto.AssignCourierRequest;
import com.delivery.dto.CreateUserRequest;
import com.delivery.dto.OrderResponse;
import com.delivery.dto.UserProfileDto;
import com.delivery.order.OrderService;
import com.delivery.order.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final OrderService orderService;

    @PostMapping("/users")
    public ResponseEntity<UserProfileDto> createUser(@Valid @RequestBody CreateUserRequest request){
        UserProfileDto userProfileDto = adminService.createUser(request);
        return new ResponseEntity<UserProfileDto>(userProfileDto, HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(@RequestParam(required = false)OrderStatus status,
                                                            Pageable pageable){
    Page<OrderResponse> orders = orderService.getAllOrders(status, pageable);
    return new ResponseEntity<Page<OrderResponse>>(orders, HttpStatus.OK);
    }

    @PutMapping("/orders/{orderId}/assign")
    public ResponseEntity<OrderResponse> assignOrder(@PathVariable UUID orderId, @Valid @RequestBody AssignCourierRequest request,
                                         @AuthenticationPrincipal UserDetails adminDetails){
    OrderResponse order = orderService.assignOrder(orderId, request.getCourierUsername(), adminDetails.getUsername());
    return new ResponseEntity<OrderResponse>(order, HttpStatus.OK);
    }
}
