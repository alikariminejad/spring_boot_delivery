package com.delivery.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String message;
    private String type;
    private UUID referenceId;
    private boolean read;
    private LocalDateTime createdAt;
}
