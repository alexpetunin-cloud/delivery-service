package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.orders.dto.OrderRequest;
import com.petunincloud.delivery.service.orders.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import com.petunincloud.delivery.service.orders.entity.OrderItemEntity;
import com.petunincloud.delivery.service.restaurants.dish.DishService;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
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
            OrderRepository repository,
            OrderMapper mapper,
            DishService dishService
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

    public OrderResponse create(OrderRequest request) {
        OrderEntity entity = mapper.toEntity(request);
        entity.setDateTime(LocalDateTime.now().withNano(0));
        entity.setStatus(OrderStatus.PENDING);

        List<OrderItemEntity> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            DishResponse dish = dishService.getDishById(itemReq.dishId());

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

    public OrderResponse confirmOrder(Long orderId) {
        OrderEntity order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be confirmed");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        return mapper.toResponse(repository.save(order));
    }

    public OrderResponse startCooking(Long orderId) {
        OrderEntity order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED orders can be start cooking");
        }

        order.setStatus(OrderStatus.COOKING);
        return mapper.toResponse(repository.save(order));
    }

    public OrderResponse markAsReady(Long orderId) {
        OrderEntity order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.COOKING) {
            throw new IllegalStateException("Only COOKING orders can be marked as ready");
        }

        order.setStatus(OrderStatus.READY);
        return mapper.toResponse(repository.save(order));
    }

    public OrderResponse startDelivery(Long orderId) {
        OrderEntity order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Only READY orders can be taken for delivery");
        }

        order.setStatus(OrderStatus.DELIVERING);
        return mapper.toResponse(repository.save(order));
    }

    public OrderResponse completeDelivery(Long orderId) {
        OrderEntity order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.DELIVERING) {
            throw new IllegalStateException("Only DELIVERING orders can be completed");
        }

        order.setStatus(OrderStatus.DELIVERED);
        return mapper.toResponse(repository.save(order));
    }

    public OrderResponse cancelOrder(Long orderId) {
        OrderEntity order = getOrderById(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot cancel DELIVERED or already CANCELED order");
        }

        order.setStatus(OrderStatus.CANCELED);
        return mapper.toResponse(repository.save(order));
    }

    private OrderEntity getOrderById(Long orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }
}