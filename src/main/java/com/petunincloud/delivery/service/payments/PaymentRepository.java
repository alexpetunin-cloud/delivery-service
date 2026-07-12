package com.petunincloud.delivery.service.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    @Query("""
        SELECT p FROM PaymentEntity p
        WHERE p.user.id = COALESCE(:userId, p.user.id)
          AND p.order.id = COALESCE(:orderId, p.order.id)
          AND p.status = COALESCE(:status, p.status)
          AND p.createdAt >= COALESCE(:fromDate, p.createdAt)
          AND p.createdAt <= COALESCE(:toDate, p.createdAt)
        """)
    List<PaymentEntity> searchAllByFilter(
            @Param("userId") Long userId,
            @Param("orderId") Long orderId,
            @Param("status") PaymentStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}
