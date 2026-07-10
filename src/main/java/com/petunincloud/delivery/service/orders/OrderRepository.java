package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("""
            SELECT o FROM OrderEntity o
            WHERE (:userId IS NULL OR o.user.id = :userId)
                AND (:restaurantId IS NULL OR o.restaurant.id = :restaurantId)
            """)
    List<OrderEntity> searchAllByFilter(
            @Param("userId") Long userId,
            @Param("restaurantId") Long restaurantId,
            Pageable pageable
    );
}