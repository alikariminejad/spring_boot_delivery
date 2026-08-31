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
public class CourierAssignedEvent implements Serializable {
    private UUID orderId;
    private UUID courierId;
    private String courierUsername;
    private LocalDateTime assignedAt;
}