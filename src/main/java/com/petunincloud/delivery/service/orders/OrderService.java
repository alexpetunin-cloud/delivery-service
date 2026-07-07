package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.orders.dto.CreateOrderRequest;
import com.petunincloud.delivery.service.orders.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import com.petunincloud.delivery.service.orders.entity.OrderItemEntity;
import com.petunincloud.delivery.service.restaurants.DishDto;
import com.petunincloud.delivery.service.restaurants.DishService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService extends BaseService<OrderEntity, OrderResponse, OrderSearchFilter> {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final DishService dishService;

    public OrderService(
            OrderRepository repository, OrderMapper mapper, DishService dishService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.dishService = dishService;
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
        entity.setDateTime(LocalDateTime.now().withNano(0));
        entity.setStatus(OrderStatus.PENDING);

        List<OrderItemEntity> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            DishDto dish = dishService.getDishById(itemReq.dishId());

            OrderItemEntity item = new OrderItemEntity();
            item.setDishId(dish.id());
            item.setDishName(dish.name());
            item.setQuantity(itemReq.quantity());
            item.setPrice(dish.price());
            item.setOrder(entity);

            items.add(item);

            totalPrice = totalPrice.add(
                    dish.price()
                    .multiply(BigDecimal.valueOf(itemReq.quantity()))
            );
        }

        entity.setItems(items);
        entity.setTotalPrice(totalPrice);

        OrderEntity saved = repository.save(entity);

        return mapper.toResponse(saved);
    }
}