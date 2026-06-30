package com.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String message;
    private String type;
    private UUID referenceId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
