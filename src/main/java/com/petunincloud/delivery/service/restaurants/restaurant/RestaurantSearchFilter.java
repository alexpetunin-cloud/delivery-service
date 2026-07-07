package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.common.BaseFilter;

public record RestaurantSearchFilter (
        String name,
        Integer pageSize,
        Integer pageNumber
) implements BaseFilter {
}