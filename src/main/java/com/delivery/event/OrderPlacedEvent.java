package com.delivery.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private UUID oderId;
    private UUID customerId;
    private String customerUsername;
    private BigDecimal price;
    private LocalDateTime createdAt;
}
