package com.delivery.controller;

import com.delivery.dto.OrderResponse;
import com.delivery.order.Order;
import com.delivery.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courier")
@PreAuthorize("hasRole('COURIER')")
@RequiredArgsConstructor
public class CourierController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getMyAssignedOrders(@AuthenticationPrincipal UserDetails userDetails,
                                                           Pageable pageable){
        Page<OrderResponse> orders = orderService.getCourierOrders(userDetails.getUsername(), pageable);
        return new ResponseEntity<Page<OrderResponse>>(orders, HttpStatus.OK);
    }
}
