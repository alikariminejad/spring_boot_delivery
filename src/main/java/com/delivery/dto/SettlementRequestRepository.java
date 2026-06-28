package com.delivery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementRequestRepository {
    @NotNull
    @DecimalMin(value = "10.00", message = "Minimum withdrawal is 10.00")
    private BigDecimal amount;
}
