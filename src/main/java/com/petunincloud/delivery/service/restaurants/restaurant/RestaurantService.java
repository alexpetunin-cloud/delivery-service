package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.restaurants.dish.DishEntity;
import com.petunincloud.delivery.service.restaurants.dish.DishMapper;
import com.petunincloud.delivery.service.restaurants.dish.DishRepository;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishRequest;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantRequest;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RestaurantService extends BaseService<RestaurantEntity, RestaurantResponse, RestaurantSearchFilter> {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;


    public RestaurantService(
            RestaurantRepository restaurantRepository,
            RestaurantMapper restaurantMapper,
            DishRepository dishRepository,
            DishMapper dishMapper
    ) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
    }

    @Override
    protected List<RestaurantEntity> findWithFilter(RestaurantSearchFilter filter, Pageable pageable) {
        return restaurantRepository.searchAllByFilter(
                filter.name(),
                pageable
        );
    }

    @Override
    protected RestaurantMapper getMapper() {
        return restaurantMapper;
    }

    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        RestaurantEntity entity = restaurantMapper.toEntity(request);
        RestaurantEntity saved = restaurantRepository.save(entity);
        return restaurantMapper.toResponse(saved);
    }

    public DishResponse addDishToRestaurant(Long restaurantId, DishRequest request) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));

        DishEntity dish = new DishEntity();
        dish.setName(request.name());
        dish.setPrice(request.price());
        dish.setRestaurant(restaurant);

        DishEntity savedDish = dishRepository.save(dish);

        restaurant.getMenu().add(savedDish);
        restaurantRepository.save(restaurant);

        return dishMapper.toResponse(savedDish);
    }
}
