package com.petunincloud.delivery.service.restaurants.dish.dto;

import java.math.BigDecimal;

public record DishResponse(
        Long id,
        String name,
        BigDecimal price
) {}
