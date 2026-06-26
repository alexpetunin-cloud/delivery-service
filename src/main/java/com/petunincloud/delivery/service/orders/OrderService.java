package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OrderService extends BaseService<OrderEntity, OrderDto, OrderSearchFilter> {

    private final OrderRepository repository;
    private final OrderMapper mapper;

    public OrderService(OrderRepository repository, OrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected List<OrderEntity> findWithFilter(OrderSearchFilter filter, Pageable pageable) {
        return repository.searchAllByFilter(
                filter.orderId(),
                filter.userId(),
                pageable
        );
    }

    @Override
    protected OrderMapper getMapper() {
        return mapper;
    }

}