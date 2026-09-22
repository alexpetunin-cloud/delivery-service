package com.petunincloud.delivery.service.deliveries.courier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCourierRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6)
        String password,

        @NotBlank
        String name,

        @NotBlank
        @Pattern(
                regexp = "^\\+?\\d{10,15}$",
                message = "Phone number must be valid"
        )
        String phone,

        @NotBlank
        String address
) {}
