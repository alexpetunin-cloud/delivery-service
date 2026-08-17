package com.petunincloud.delivery.service.restaurants.dish;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService extends BaseService<DishEntity, DishResponse, DishSearchFilter> {
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final static Logger log = LoggerFactory.getLogger(DishService.class);

    public DishService(
            DishRepository dishRepository,
            DishMapper dishMapper
    ) {
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
    }

    public DishResponse getDishById(Long dishId) {
        log.info("Get dish by id: {}", dishId);
        long startTime = System.currentTimeMillis();

        try {
            DishEntity entity = dishRepository.findById(dishId)
                    .orElseThrow(() -> {
                        log.warn("Dish not found: {}", dishId);
                        return new IllegalArgumentException("Dish not found: " + dishId);
                    });

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success get dish by id: {}, duration={}ms", dishId, duration);

            return dishMapper.toResponse(entity);

        } catch (Exception e) {
            log.error("Failed get dish by id: {}. Error: {}", dishId, e.getMessage());
            throw e;
        }
    }

    @Override
    protected List<DishEntity> findWithFilter(DishSearchFilter filter, Pageable pageable) {
        log.debug("Searching dishes with filter: {}, pageable: {}", filter, pageable);
        return dishRepository.searchAllByFilter(
                filter.name(),
                filter.restaurantId(),
                pageable
        );
    }

    @Override
    protected DishMapper getMapper() {
        return dishMapper;
    }
}