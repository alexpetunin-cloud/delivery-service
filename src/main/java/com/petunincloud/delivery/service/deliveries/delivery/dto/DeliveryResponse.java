package com.petunincloud.delivery.service.deliveries.delivery.dto;

import com.petunincloud.delivery.service.deliveries.delivery.DeliveryStatus;

import java.time.LocalDateTime;

public record DeliveryResponse(
        Long id,
        Long orderId,
        Long courierId,
        String courierName,
        DeliveryStatus status,
        String pickupAddress,
        String deliveryAddress,
        LocalDateTime assignedAt,
        LocalDateTime deliveredAt
) {}
