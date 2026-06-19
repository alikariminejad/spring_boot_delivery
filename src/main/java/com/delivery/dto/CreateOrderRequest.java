package com.delivery.dto;

import com.delivery.order.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull @Valid
    private Address origin;

    @NotNull @Valid
    private Address destination;

    @Positive
    private Double weight;

    private String dimensions;
    private String description;
}
