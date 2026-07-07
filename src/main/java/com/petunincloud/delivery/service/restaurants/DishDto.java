package com.petunincloud.delivery.service.restaurants;

import java.math.BigDecimal;

public record DishDto(
        Long id,
        String name,
        BigDecimal price
) {}
