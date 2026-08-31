package com.delivery.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusChangedEvent implements Serializable {
    private UUID orderId;
    private UUID customerId;
    private String customerUsername;
    private UUID courierId;
    private String courierUsername;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime changedAt;
}