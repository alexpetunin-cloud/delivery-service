package com.petunincloud.delivery.service.restaurants.dish;

import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class DishRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DishRepository dishRepository;

    private RestaurantEntity restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new RestaurantEntity();
        restaurant.setName("Додо Пицца");
        restaurant.setAddress("пр. Калинина 8");
        entityManager.persist(restaurant);

        createDish("Маргарита", BigDecimal.valueOf(400), restaurant);
        createDish("Четыре сыра", BigDecimal.valueOf(500), restaurant);
        createDish("Мясник", BigDecimal.valueOf(600), restaurant);

        entityManager.flush();
        entityManager.clear();
    }

    private void createDish(
            String name,
            BigDecimal price,
            RestaurantEntity restaurant
    ) {
        DishEntity dish = new DishEntity();
        dish.setName(name);
        dish.setPrice(price);
        dish.setRestaurant(restaurant);

        entityManager.persist(dish);
    }

    @Test
    void searchAllByFilter_ShouldReturnDishByName() {
        Pageable pageable = PageRequest.of(0, 5);

        List<DishEntity> dishes = dishRepository.searchAllByFilter(
                "Маргарита",
                null,
                pageable
        );

        assertThat(dishes).hasSize(1);
        assertThat(dishes.get(0).getName()).isEqualTo("Маргарита");
    }

    @Test
    void searchAllByFilter_ShouldReturnDishByRestaurantId() {
        Pageable pageable = PageRequest.of(0, 5);
        Long restaurantId = restaurant.getId();

        List<DishEntity> dishes = dishRepository.searchAllByFilter(
                null,
                restaurantId,
                pageable
        );

        assertThat(dishes).hasSize(3);
        assertThat(dishes.get(0).getRestaurant().getId()).isEqualTo(restaurantId);
    }

    @Test
    void searchAllByFilter_ShouldApplyPagination() {
        Pageable pageable = PageRequest.of(0, 2);

        List<DishEntity> dishes = dishRepository.searchAllByFilter(
                null,
                null,
                pageable
        );

        assertThat(dishes).hasSize(2);
    }
}
