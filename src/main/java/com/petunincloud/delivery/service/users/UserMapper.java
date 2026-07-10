package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.users.dto.UserRequest;
import com.petunincloud.delivery.service.users.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements BaseMapper<UserEntity, UserResponse>{

    public UserEntity toEntity(UserRequest request) {
        UserEntity entity = new UserEntity();
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setName(request.name());
        entity.setAddress(request.address());
        entity.setApartment(request.apartment());
        entity.setDeliveryInstructions(request.deliveryInstructions());
        return entity;
    }

    @Override
    public UserResponse toResponse(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getName(),
                entity.getAddress(),
                entity.getApartment(),
                entity.getDeliveryInstructions()
        );
    }

    @Override
    public UserEntity toEntity(UserResponse dto) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
