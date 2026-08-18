package com.delivery.dto;

import com.delivery.order.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String status;
    private BigDecimal price;
    private Address origin;
    private Address destination;
    private Double weight;
    private String dimensions;
    private String description;
    private LocalDateTime createdAt;
}
