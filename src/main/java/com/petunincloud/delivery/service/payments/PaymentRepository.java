package com.petunincloud.delivery.service.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    @Query("""
            SELECT p FROM PaymentEntity p
            WHERE (:userId IS NULL OR p.userId = :userId)
              AND (:orderId IS NULL OR p.order.id = :orderId)
              AND (:status IS NULL OR p.status = :status)
              AND (:fromDate IS NULL OR p.createdAt >= :fromDate)
              AND (:toDate IS NULL OR p.createdAt <= :toDate)
              AND (:minAmount IS NULL OR p.amount >= :minAmount)
              AND (:maxAmount IS NULL OR p.amount <= :maxAmount)
            """)
    List<PaymentEntity> searchAllByFilter(
            @Param("userId") Long userId,
            @Param("orderId") Long orderId,
            @Param("status") PaymentStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );
}
