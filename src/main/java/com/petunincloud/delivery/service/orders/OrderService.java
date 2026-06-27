package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.orders.dto.CreateOrderRequest;
import com.petunincloud.delivery.service.orders.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService extends BaseService<OrderEntity, OrderResponse, OrderSearchFilter> {

    private final OrderRepository repository;
    private final OrderMapper mapper;

    public OrderService(OrderRepository repository, OrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected List<OrderEntity> findWithFilter(OrderSearchFilter filter, Pageable pageable) {
        return repository.searchAllByFilter(
                filter.userId(),
                pageable
        );
    }

    @Override
    protected OrderMapper getMapper() {
        return mapper;
    }

    public OrderResponse create(CreateOrderRequest request) {

        OrderEntity entity = mapper.toEntity(request);

        entity.setDateTime(LocalDateTime.now());
        entity.setStatus(OrderStatus.PENDING);

        // Необходимо создать логику подсчета средств (totalPrice)

        OrderEntity saved = repository.save(entity);

        return mapper.toResponse(saved);
    }
}