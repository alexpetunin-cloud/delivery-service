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
            WHERE (:orderId IS NULL OR d.order.id = :orderId)
              AND (:courierId IS NULL OR d.courier.id = :courierId)
              AND (:status IS NULL OR d.status = :status)
              AND (:fromDate IS NULL OR d.assignedAt >= :fromDate)
              AND (:toDate IS NULL OR d.assignedAt <= :toDate)
            """)
    List<DeliveryEntity> searchAllByFilter(
            @Param("orderId") Long orderId,
            @Param("courierId") Long courierId,
            @Param("status") DeliveryStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
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
}
