package com.delivery.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelledEvent {

    private UUID orderId;
    private UUID customerId;
    private String customerUsername;
    private LocalDateTime cancelledAt;
}
