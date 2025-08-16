package com.youssef.socialnetwork.auth.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Size(min=3, max=50) String username,
        @Email @NotBlank String email,
        @NotBlank @Size(min=6, max=100) String password
) {}