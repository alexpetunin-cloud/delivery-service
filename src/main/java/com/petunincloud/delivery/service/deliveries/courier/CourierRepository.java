package com.petunincloud.delivery.service.deliveries.courier;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourierRepository extends JpaRepository<CourierEntity, Long> {
    @Query("""
        SELECT c FROM CourierEntity c
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(COALESCE(CONCAT('%', :name, '%'), '')))
          AND (:phone IS NULL OR c.phone = :phone)
          AND (:status IS NULL OR c.status = :status)
        """)
    List<CourierEntity> searchAllByFilter(
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("status") CourierStatus status,
            Pageable pageable
    );

    Optional<CourierEntity> findTopByStatus(CourierStatus status);
}
