package com.petunincloud.delivery.service.restaurants.restaurant.dto;

import jakarta.validation.constraints.NotBlank;

public record RestaurantRequest(
        @NotBlank(message = "Restaurant name is required")
        String name,

        @NotBlank(message = "Restaurant address is required")
        String address
) {}