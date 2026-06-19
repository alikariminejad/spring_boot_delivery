package com.delivery.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class Address {
    @NotBlank
    @Column(length = 255,nullable = false)
    private String street;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String city;

    @Column(length = 100)
    private String state;

    @NotBlank
    @Column(length = 20, nullable = false)
    private String zipCode;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String country;

}
