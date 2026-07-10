package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.common.BaseFilter;

import java.time.LocalDateTime;

public record DeliverySearchFilter(
        Long orderId,
        Long courierId,
        DeliveryStatus status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Integer pageSize,
        Integer pageNumber
) implements BaseFilter {}
