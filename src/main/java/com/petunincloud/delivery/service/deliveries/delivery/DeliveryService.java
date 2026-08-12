package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.deliveries.courier.CourierEntity;
import com.petunincloud.delivery.service.deliveries.courier.CourierRepository;
import com.petunincloud.delivery.service.deliveries.courier.CourierStatus;
import com.petunincloud.delivery.service.deliveries.delivery.dto.DeliveryResponse;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.order.OrderService;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.orders.order.OrderEntity;
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

    public DeliveryService(
            DeliveryRepository deliveryRepository,
            CourierRepository courierRepository,
            OrderRepository orderRepository,
            OrderService orderService,
            DeliveryMapper deliveryMapper
    ) {
        this.deliveryRepository = deliveryRepository;
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.deliveryMapper = deliveryMapper;
    }

    @Override
    protected List<DeliveryEntity> findWithFilter(DeliverySearchFilter filter, Pageable pageable) {
        log.debug("Searching deliveries with filter: {}, pageable: {}", filter, pageable);

        return deliveryRepository.searchAllByFilter(
                filter.orderId(),
                filter.courierId(),
                filter.status(),
                filter.assignedAt(),
                filter.deliveredAt(),
                pageable
        );
    }

    @Override
    protected DeliveryMapper getMapper() {
        return deliveryMapper;
    }

    @Transactional
    public DeliveryResponse assignCourierToOrder(Long orderId) {
        log.info("Assigning courier to order={}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderService.getOrderById(orderId);

            if (order.getStatus() == OrderStatus.DELIVERING || order.getStatus() == OrderStatus.DELIVERED) {
                log.warn("Order is already in delivery process (status={})", order.getStatus());
                throw new IllegalStateException("Order is already in delivery process");
            }
            if (order.getStatus() != OrderStatus.READY) {
                log.warn("Order must be READY for delivery (status={})", order.getStatus());
                throw new IllegalStateException("Order must be READY for delivery");
            }

            if (deliveryRepository.existsByOrderId(orderId)) {
                log.warn("Delivery already assigned for this order={}", orderId);
                throw new IllegalStateException("Delivery already assigned for this order");
            }

            CourierEntity courier = courierRepository.findTopByStatus(CourierStatus.AVAILABLE)
                    .orElseThrow(() -> {
                        log.warn("No available couriers");
                        return new IllegalStateException("No available couriers");
                    });

            DeliveryEntity delivery = new DeliveryEntity();
            delivery.setOrder(order);
            delivery.setCourier(courier);
            delivery.setStatus(DeliveryStatus.ASSIGNED);
            log.debug("Status delivery set of ASSIGNED (order={})", delivery.getOrder().getId());
            delivery.setAssignedAt(LocalDateTime.now().withNano(0));
            delivery.setPickupAddress(order.getRestaurant().getAddress());
            delivery.setDeliveryAddress(order.getUser().getAddress());

            courier.setStatus(CourierStatus.BUSY);
            log.debug("Status courier set of BUSY (id={}, name={})",
                    courier.getId(), courier.getName());
            order.setStatus(OrderStatus.DELIVERING);
            log.debug("Status order set of DELIVERING (id={})", orderId);

            courierRepository.save(courier);
            orderRepository.save(order);
            DeliveryEntity saved = deliveryRepository.save(delivery);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success assign courier (name={}) to order={}, duration={}ms",
                    courier.getName(), orderId, duration);

            return deliveryMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed assign courier to order={}. Error: {}",
                    orderId, e.getMessage());
            throw e;
        }
    }

    public DeliveryResponse getDeliveryById(Long id) {
        log.info("Getting delivery by id={}", id);
        long startTime = System.currentTimeMillis();

        try {
            DeliveryEntity delivery = deliveryRepository.findByIdWithOrderAndCourier(id)
                    .orElseThrow(() -> {
                        log.warn("Delivery not found: id={}", id);
                        return new IllegalArgumentException("Delivery not found: " + id);
                    });

            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully get delivery={}, duration={}ms", id, duration);

            return deliveryMapper.toResponse(delivery);

        } catch (Exception e) {
            log.error("Failed to get delivery={}. Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public DeliveryResponse completeDelivery(Long deliveryId) {
        log.info("Completing delivery for id={}", deliveryId);
        long startTime = System.currentTimeMillis();

        try {
            DeliveryEntity delivery = deliveryRepository.findByIdWithOrderAndCourier(deliveryId)
                    .orElseThrow(() -> {
                        log.warn("Delivery not found: id={}", deliveryId);
                        return new IllegalArgumentException("Delivery not found: " + deliveryId);
                    });

            OrderEntity order = delivery.getOrder();
            CourierEntity courier = delivery.getCourier();

            if (order.getStatus() != OrderStatus.DELIVERING) {
                log.warn("Only DELIVERING orders can be completed (status={})", order.getStatus());
                throw new IllegalStateException("Only DELIVERING orders can be completed");
            }

            order.setStatus(OrderStatus.DELIVERED);
            log.debug("Status order={} set of DELIVERED", order.getId());
            delivery.setStatus(DeliveryStatus.DELIVERED);
            log.debug("Status delivery={} set of DELIVERED", delivery.getId());
            delivery.setDeliveredAt(LocalDateTime.now().withNano(0));
            courier.setStatus(CourierStatus.AVAILABLE);
            log.debug("Status courier (id={}, name={}) set of AVAILABLE",
                    courier.getId(), courier.getName());

            orderRepository.save(order);
            courierRepository.save(courier);
            DeliveryEntity saved = deliveryRepository.save(delivery);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success complete delivery={}, duration={}ms", deliveryId, duration);

            return deliveryMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed complete delivery={}. Error: {}", deliveryId, e.getMessage());
            throw e;
        }
    }
}
