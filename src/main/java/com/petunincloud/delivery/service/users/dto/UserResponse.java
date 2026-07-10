package com.petunincloud.delivery.service.users.dto;

public record UserResponse(
        Long id,
        String email,
        String phone,
        String name,
        String address,
        String apartment,
        String deliveryInstructions
) {}
