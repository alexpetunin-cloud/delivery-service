package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RestaurantService extends BaseService<RestaurantEntity, RestaurantResponse, RestaurantSearchFilter> {
    private final RestaurantRepository repository;
    private final RestaurantMapper mapper;

    public RestaurantService(RestaurantRepository repository, RestaurantMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected List<RestaurantEntity> findWithFilter(RestaurantSearchFilter filter, Pageable pageable) {
        return repository.searchAllByFilter(
                filter.name(),
                pageable
        );
    }

    @Override
    protected RestaurantMapper getMapper() {
        return mapper;
    }
}
