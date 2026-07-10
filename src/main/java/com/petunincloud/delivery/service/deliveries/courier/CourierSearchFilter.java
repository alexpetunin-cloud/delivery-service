package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.common.BaseFilter;

public record CourierSearchFilter(
        String name,
        String phone,
        CourierStatus status,
        Integer pageSize,
        Integer pageNumber
) implements BaseFilter {}
