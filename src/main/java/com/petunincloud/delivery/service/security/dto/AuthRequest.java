package com.petunincloud.delivery.service.security.dto;

public record AuthRequest(
        String email,
        String password,
        String name,
        String phone,
        String address
) {}
