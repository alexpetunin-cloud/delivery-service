package com.petunincloud.delivery.service.restaurants.restaurant.dto;

import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;

import java.util.List;

public record RestaurantResponse(
        Long id,
        String name,
        String address,
        List<DishResponse> menu
) {}