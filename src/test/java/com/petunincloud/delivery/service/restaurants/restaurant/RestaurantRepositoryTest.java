package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.restaurants.dish.DishEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RestaurantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        createRestaurant("Лоло", "ул. Щадрина, 5", List.of());
        createRestaurant("МятаЧай", "ул. Зеленая, 15", List.of());
        createRestaurant("Рустик", "ул. Димитрова, 25", List.of());

        entityManager.flush();
        entityManager.clear();
    }

    private void createRestaurant(
            String name,
            String address,
            List<DishEntity> menu
    ) {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setMenu(menu);

        entityManager.persist(restaurant);
    }

    @Test
    void searchAllByFilter_ShouldReturnRestaurantByName() {
        Pageable pageable = PageRequest.of(0, 5);

        List<RestaurantEntity> restaurants = restaurantRepository.searchAllByFilter(
                "Мята",
                pageable
        );

        assertThat(restaurants).hasSize(1);
        assertThat(restaurants.get(0).getName()).isEqualTo("МятаЧай");
    }

    @Test
    void searchAllByFilter_ShouldApplyPagination() {
        Pageable pageable = PageRequest.of(0, 2);

        List<RestaurantEntity> restaurants = restaurantRepository.searchAllByFilter(
                null,
                pageable
        );

        assertThat(restaurants).hasSize(2);
    }
}
