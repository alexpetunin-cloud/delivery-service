package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.deliveries.courier.CourierEntity;
import com.petunincloud.delivery.service.deliveries.courier.CourierRepository;
import com.petunincloud.delivery.service.deliveries.courier.CourierStatus;
import com.petunincloud.delivery.service.deliveries.delivery.dto.DeliveryResponse;
import com.petunincloud.delivery.service.orders.OrderRepository;
import com.petunincloud.delivery.service.orders.OrderService;
import com.petunincloud.delivery.service.orders.OrderStatus;
import com.petunincloud.delivery.service.orders.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService extends BaseService<DeliveryEntity, DeliveryResponse, DeliverySearchFilter> {

    private final static Logger log = LoggerFactory.getLogger(DeliveryService.class);
    private final DeliveryRepository deliveryRepository;
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final DeliveryMapper deliveryMapper;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           CourierRepository courierRepository,
                           OrderRepository orderRepository,
                           OrderService orderService,
                           DeliveryMapper deliveryMapper) {
        this.deliveryRepository = deliveryRepository;
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.deliveryMapper = deliveryMapper;
    }

    @Override
    protected List<DeliveryEntity> findWithFilter(DeliverySearchFilter filter, Pageable pageable) {
        return deliveryRepository.searchAllByFilter(
                filter.orderId(),
                filter.courierId(),
                filter.status(),
                filter.fromDate(),
                filter.toDate(),
                pageable
        );
    }

    @Override
    protected DeliveryMapper getMapper() {
        return deliveryMapper;
    }

    @Transactional
    public DeliveryResponse assignCourierToOrder(Long orderId) {
        OrderEntity order = orderService.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Order must be READY for delivery");
        }
        if (order.getStatus() == OrderStatus.DELIVERING || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Order is already in delivery process");
        }

        CourierEntity courier = courierRepository.findTopByStatus(CourierStatus.AVAILABLE)
                .orElseThrow(() -> {
                    log.warn("No available couriers for order {}", orderId);
                    return new IllegalStateException("No available couriers");
                });

        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrder(order);
        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());

        delivery.setPickupAddress(order.getRestaurant().getAddress());
        delivery.setDeliveryAddress(order.getUser().getAddress());

        courier.setStatus(CourierStatus.BUSY);
        order.setStatus(OrderStatus.DELIVERING);

        courierRepository.save(courier);
        orderRepository.save(order);
        DeliveryEntity saved = deliveryRepository.save(delivery);

        return deliveryMapper.toResponse(saved);
    }

    public DeliveryResponse getDeliveryById(Long id) {
        DeliveryEntity delivery = deliveryRepository.findByIdWithOrderAndCourier(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + id));
        return deliveryMapper.toResponse(delivery);
    }

    @Transactional
    public DeliveryResponse completeDelivery(Long deliveryId) {
        DeliveryEntity delivery = deliveryRepository.findByIdWithOrderAndCourier(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + deliveryId));

        OrderEntity order = delivery.getOrder();
        CourierEntity courier = delivery.getCourier();

        if (order.getStatus() != OrderStatus.DELIVERING) {
            throw new IllegalStateException("Only DELIVERING orders can be completed");
        }

        order.setStatus(OrderStatus.DELIVERED);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(LocalDateTime.now());
        courier.setStatus(CourierStatus.AVAILABLE);

        orderRepository.save(order);
        courierRepository.save(courier);
        DeliveryEntity saved = deliveryRepository.save(delivery);

        return deliveryMapper.toResponse(saved);
    }
}
