package com.petunincloud.delivery.service.restaurants.dish;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DishRepository extends JpaRepository<DishEntity, Long> {
    @Query("""
            SELECT d FROM DishEntity d
            WHERE (:name IS NULL OR d.name = :name)
              AND (:restaurantId IS NULL OR d.restaurant.id = :restaurantId)
            """)
    List<DishEntity> searchAllByFilter(
            @Param("name") String name,
            @Param("restaurantId") Long restaurantId,
            Pageable pageable
    );
}