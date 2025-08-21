package com.youssef.socialnetwork.dto;

public record AuthResponse(
        String token,
        String type,
        long expiresIn
) {
    public static AuthResponse bearer(String token, long seconds) {
        return new AuthResponse(token, "Bearer", seconds);
    }
}