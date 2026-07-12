package com.petunincloud.delivery.service.orders.order_item;

import com.petunincloud.delivery.service.orders.order.OrderEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "dish_id", nullable = false)
    private Long dishId;

    @Column(name = "dish_name")
    private String dishName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price; // цена за единицу на момент заказа

    public OrderItemEntity(
            OrderEntity order,
            Long dishId,
            String dishName,
            Integer quantity,
            BigDecimal price
    ) {
        this.order = order;
        this.dishId = dishId;
        this.dishName = dishName;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItemEntity() {
    }

    public Long getId() {
        return id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public Long getDishId() {
        return dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
