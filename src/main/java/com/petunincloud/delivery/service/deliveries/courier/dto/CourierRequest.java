package com.petunincloud.delivery.service.deliveries.courier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CourierRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Phone number must be valid (e.g., +79991234567)")
        String phone
) {}
