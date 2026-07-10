package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.deliveries.courier.CourierEntity;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class DeliveryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private CourierEntity courier;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;
    private String pickupAddress;
    private String deliveryAddress;

    public DeliveryEntity() {
    }

    public DeliveryEntity(
            Long id,
            OrderEntity order,
            CourierEntity courier,
            DeliveryStatus status,
            LocalDateTime assignedAt,
            LocalDateTime deliveredAt,
            String pickupAddress,
            String deliveryAddress
    ) {
        this.id = id;
        this.order = order;
        this.courier = courier;
        this.status = status;
        this.assignedAt = assignedAt;
        this.deliveredAt = deliveredAt;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
    }

    public Long getId() {
        return id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public CourierEntity getCourier() {
        return courier;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public void setCourier(CourierEntity courier) {
        this.courier = courier;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
