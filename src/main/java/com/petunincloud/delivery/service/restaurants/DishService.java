package com.petunincloud.delivery.service.restaurants;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class DishService {

    // Временная заглушка
    private static final Map<Long, DishDto> DISHES = Map.of(
            101L, new DishDto(101L, "Пицца Маргарита", BigDecimal.valueOf(150.00)),
            102L, new DishDto(102L, "Паста Карбонара", BigDecimal.valueOf(200.00)),
            103L, new DishDto(103L, "Салат Цезарь", BigDecimal.valueOf(120.00))
    );

    public DishDto getDishById(Long dishId) {
        DishDto dish = DISHES.get(dishId);
        if (dish == null) {
            throw new IllegalArgumentException("Dish not found: " + dishId);
        }
        return dish;
    }
}