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
public class OrderCancelledEvent implements Serializable {

    private UUID orderId;
    private UUID customerId;
    private String customerUsername;
    private LocalDateTime cancelledAt;
}
