package com.petunincloud.delivery.service.orders;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "orders")
@Entity
public class OrderEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    // Список заказа

    @Column(name = "status", nullable = false)
    private OrderStatus status;

    public OrderEntity() {
    }

    public OrderEntity(
            Long orderId,
            Long userId,
            LocalDateTime dateTime,
            OrderStatus status
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.dateTime = dateTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
