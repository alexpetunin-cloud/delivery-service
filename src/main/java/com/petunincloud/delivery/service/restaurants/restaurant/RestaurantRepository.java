package com.petunincloud.delivery.service.restaurants.restaurant;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
    @Query(value = """
        SELECT * FROM restaurants r
        WHERE (:name IS NULL OR r.name ILIKE COALESCE(CONCAT('%', :name, '%'), ''))
        """, nativeQuery = true)
    List<RestaurantEntity> searchAllByFilter(
            @Param("name") String name,
            Pageable pageable
    );
}
