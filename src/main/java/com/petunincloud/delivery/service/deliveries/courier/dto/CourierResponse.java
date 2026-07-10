package com.petunincloud.delivery.service.deliveries.courier.dto;

import com.petunincloud.delivery.service.deliveries.courier.CourierStatus;

public record CourierResponse(
        Long id,
        String name,
        String phone,
        CourierStatus status
) {}
