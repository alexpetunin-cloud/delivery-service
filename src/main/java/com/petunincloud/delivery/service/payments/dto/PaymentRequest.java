package com.petunincloud.delivery.service.payments.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "User ID is required")
        Long userId
) {}
