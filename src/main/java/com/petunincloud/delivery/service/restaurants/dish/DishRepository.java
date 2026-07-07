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
            """)
    List<DishEntity> searchAllByFilter(
            @Param("name") String name,
            Long restaurantId,
            Pageable pageable
    );
}