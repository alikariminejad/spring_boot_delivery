package com.delivery.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SettlementRequestResponse {
    private UUID id;
    private String courierUsername;
    private BigDecimal amount;
    private String status;
    @Nullable
    private String processedByUsername;
    private LocalDateTime processedAt;
    private String note;
    private LocalDateTime createdAt;
}
