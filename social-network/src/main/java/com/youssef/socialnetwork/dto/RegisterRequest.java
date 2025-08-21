package com.youssef.socialnetwork.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        String username,

        @Email(message = "Email must be valid")
        String email,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password) {}