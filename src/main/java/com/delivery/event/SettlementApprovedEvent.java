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
public class SettlementApprovedEvent {

    private UUID settlementId;
    private UUID courierId;
    private String courierUsername;
    private BigDecimal amount;
    private LocalDateTime approvedAt;
}
