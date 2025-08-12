package com.udacity.ecommerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String confirmPassword
) {}
