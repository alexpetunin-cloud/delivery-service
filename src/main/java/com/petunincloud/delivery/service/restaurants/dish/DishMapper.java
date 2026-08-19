package com.petunincloud.delivery.service.restaurants.dish;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import org.springframework.stereotype.Component;

@Component
public class DishMapper implements BaseMapper<DishEntity, DishResponse> {

    @Override
    public DishResponse toResponse(DishEntity entity) {
        return new DishResponse(
                entity.getId(),
                entity.getName(),
                entity.getPrice()
        );
    }

    @Override
    public DishEntity toEntity(DishResponse dto) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
