package com.petunincloud.delivery.service.restaurants.dish;

import com.petunincloud.delivery.service.common.BaseFilter;

public record DishSearchFilter(
        String name,
        Long restaurantId,
        Integer pageSize,
        Integer pageNumber
) implements BaseFilter {
}
