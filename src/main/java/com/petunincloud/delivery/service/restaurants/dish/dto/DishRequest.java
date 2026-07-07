package com.petunincloud.delivery.service.restaurants.dish.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DishRequest(
        @NotBlank(message = "The name of the dish is required")
        String name,

        @NotNull(message = "The price is mandatory")
        @Positive(message = "The price should be positive")
        BigDecimal price
) {}
