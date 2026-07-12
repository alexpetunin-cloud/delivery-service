package com.petunincloud.delivery.service.deliveries.courier;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourierRepository extends JpaRepository<CourierEntity, Long> {
    @Query(value = """
        SELECT * FROM couriers c
        WHERE (:name IS NULL OR c.name ILIKE COALESCE(CONCAT('%', :name, '%'), ''))
          AND (:phone IS NULL OR c.phone = :phone)
          AND (:status IS NULL OR c.status = :status)
        """, nativeQuery = true)
    List<CourierEntity> searchAllByFilter(
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("status") String status,
            Pageable pageable
    );

    Optional<CourierEntity> findTopByStatus(CourierStatus status);
}
