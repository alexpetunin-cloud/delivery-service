package com.petunincloud.delivery.service.orders.order;

import com.petunincloud.delivery.service.orders.order_item.OrderItemEntity;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantEntity;
import com.petunincloud.delivery.service.users.UserEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Entity
public class OrderEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    public OrderEntity() {
    }

    public OrderEntity(
            Long id,
            UserEntity user,
            RestaurantEntity restaurant,
            LocalDateTime dateTime,
            List<OrderItemEntity> items,
            OrderStatus status,
            BigDecimal totalPrice
    ) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.dateTime = dateTime;
        this.items = items;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
