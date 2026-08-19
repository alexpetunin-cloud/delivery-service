package com.petunincloud.delivery.service.deliveries.delivery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {

    @Query("""
        SELECT d FROM DeliveryEntity d
          WHERE d.order.id = COALESCE(:orderId, d.order.id)
          AND d.courier.id = COALESCE(:courierId, d.courier.id)
          AND d.status = COALESCE(:status, d.status)
          AND d.assignedAt >= COALESCE(:assignedAt, d.assignedAt)
          AND d.deliveredAt <= COALESCE(:deliveredAt, d.deliveredAt)
        """)
    List<DeliveryEntity> searchAllByFilter(
            @Param("orderId") Long orderId,
            @Param("courierId") Long courierId,
            @Param("status") DeliveryStatus status,
            @Param("assignedAt") LocalDateTime assignedAt,
            @Param("deliveredAt") LocalDateTime deliveredAt,
            Pageable pageable
    );

    @Query("""
        SELECT d FROM DeliveryEntity d
        JOIN FETCH d.order
        JOIN FETCH d.courier
        WHERE d.id = :id
        """)
    Optional<DeliveryEntity> findByIdWithOrderAndCourier(
            @Param("id") Long id
    );

    boolean existsByOrderId(Long orderId);
}
