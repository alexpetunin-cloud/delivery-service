package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseFilter;

public record OrderSearchFilter(
        Long orderId,
        Long userId,
        Integer pageSize,
        Integer pageNumber
) implements BaseFilter {
}