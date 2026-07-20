package com.delivery.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierAssignedEvent {

    private UUID orderId;
    private UUID courierId;
    private String courierUsername;
    private LocalDateTime assignedAt;
}
