package com.petunincloud.delivery.service.payments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}
