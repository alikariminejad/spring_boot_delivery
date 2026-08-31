package com.delivery.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent implements Serializable {
    private UUID orderId;
    private UUID customerId;
    private String customerUsername;
    private BigDecimal price;
    private LocalDateTime createdAt;
}