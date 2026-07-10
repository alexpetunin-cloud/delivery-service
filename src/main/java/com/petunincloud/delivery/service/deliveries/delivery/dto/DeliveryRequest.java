package com.petunincloud.delivery.service.deliveries.delivery.dto;

import jakarta.validation.constraints.NotNull;

public record DeliveryRequest(
        @NotNull(message = "Order ID is required")
        Long orderId
) {}
