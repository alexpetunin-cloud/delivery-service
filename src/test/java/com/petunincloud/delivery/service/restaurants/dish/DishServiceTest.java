package com.petunincloud.delivery.service.restaurants.dish;

import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishMapper dishMapper;

    @InjectMocks
    private DishService dishService;

    @Test
    void getDishById_ShouldReturnDish() {
        Long dishId = 1L;

        DishEntity dish = new DishEntity();

        DishResponse dishResponse = new DishResponse(
                dishId,
                "Маргарита",
                BigDecimal.valueOf(300)
        );

        when(dishRepository.findById(dishId))
                .thenReturn(Optional.of(dish));
        when(dishMapper.toResponse(dish))
                .thenReturn(dishResponse);

        DishResponse result = dishService.getDishById(dishId);

        assertNotNull(result);

        verify(dishRepository, times(1))
                .findById(dishId);
        verify(dishMapper, times(1))
                .toResponse(dish);
    }

    @Test
    void getDishById_ShouldThrowException_WhenDishNotFound() {
        Long dishId = 1L;

        when(dishRepository.findById(dishId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> dishService.getDishById(dishId));
    }
}
