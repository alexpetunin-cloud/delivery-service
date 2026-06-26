package com.petunincloud.delivery.service.orders;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("""
            SELECT o FROM OrderEntity o
            WHERE (:orderId IS NULL OR o.orderId = :orderId)
              AND (:userId IS NULL OR o.userId = :userId)
            """)
    List<OrderEntity> searchAllByFilter(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId,
            Pageable pageable
    );
}