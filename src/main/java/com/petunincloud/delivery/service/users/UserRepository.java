package com.petunincloud.delivery.service.users;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = """
        SELECT * FROM users u
        WHERE (:name IS NULL OR u.name ILIKE CONCAT('%', :name, '%'))
          AND (:email IS NULL OR u.email = :email)
          AND (:phone IS NULL OR u.phone = :phone)
          AND (:address IS NULL OR u.address ILIKE CONCAT('%', :address, '%'))
        """, nativeQuery = true)
    List<UserEntity> searchAllByFilter(
            @Param("name") String name,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("address") String address,
            Pageable pageable
    );

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhone(String phone);
}
