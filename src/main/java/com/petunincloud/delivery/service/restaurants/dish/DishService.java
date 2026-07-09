package com.petunincloud.delivery.service.restaurants.dish;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService extends BaseService<DishEntity, DishResponse, DishSearchFilter> {
    private final DishRepository dishRepository;
    private final DishMapper mapper;

    public DishService(DishRepository dishRepository, DishMapper mapper) {
        this.dishRepository = dishRepository;
        this.mapper = mapper;
    }

    public DishResponse getDishById(Long dishId) {
        DishEntity entity = dishRepository.findById(dishId)
                .orElseThrow(() -> new IllegalArgumentException("Dish not found: " + dishId));

        return mapper.toResponse(entity);
    }

    @Override
    protected List<DishEntity> findWithFilter(DishSearchFilter filter, Pageable pageable) {
        return dishRepository.searchAllByFilter(
                filter.name(),
                filter.restaurantId(),
                pageable
        );
    }

    @Override
    protected DishMapper getMapper() {
        return mapper;
    }
}