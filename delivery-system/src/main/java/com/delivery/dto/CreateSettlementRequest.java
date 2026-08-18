package com.delivery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSettlementRequest {
    @NotNull
    @DecimalMin(value = "10.00", message = "Minimum withdrawal is 10.00")
    private BigDecimal amount;
}
