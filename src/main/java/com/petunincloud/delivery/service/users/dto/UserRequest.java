package com.petunincloud.delivery.service.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        String phone,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Delivery address is required")
        String address,

        String apartment, // необязательно

        String deliveryInstructions // необязательно
) {}
