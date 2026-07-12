package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseFilter;

import java.time.LocalDateTime;

public record PaymentSearchFilter(
        Long userId,
        Long orderId,
        PaymentStatus status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Integer pageSize,
        Integer pageNumber
) implements BaseFilter {}
