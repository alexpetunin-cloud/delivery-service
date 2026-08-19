package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.restaurants.dish.DishEntity;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantRequest;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantMapper implements BaseMapper<RestaurantEntity, RestaurantResponse> {

    @Override
    public RestaurantResponse toResponse(RestaurantEntity entity) {
        List<DishResponse> itemResponses = entity.getMenu().stream()
                .map(this::toDishResponse)
                .toList();

        return new RestaurantResponse(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                itemResponses
        );
    }

    @Override
    public RestaurantEntity toEntity(RestaurantResponse dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private DishResponse toDishResponse(DishEntity item) {
        return new DishResponse(
                item.getId(),
                item.getName(),
                item.getPrice()
        );
    }

    public RestaurantEntity toEntity(RestaurantRequest request) {
        RestaurantEntity restaurant = new RestaurantEntity();

        restaurant.setName(request.name());
        restaurant.setAddress(request.address());

        return restaurant;
    }
}
