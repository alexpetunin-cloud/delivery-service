package com.petunincloud.delivery.service.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserPatchRequest(
        @Email(message = "Invalid email format")
        String email,

        @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Phone number must be valid (e.g., +79991234567)")
        String phone,

        String name,
        String address,
        String apartment,
        String deliveryInstructions
) {}
