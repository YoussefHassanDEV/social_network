package com.youssef.socialnetwork.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youssef.socialnetwork.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) {
        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            ApiError apiError = new ApiError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    "Authentication is required to access this resource",
                    LocalDateTime.now(),
                    request.getRequestURI()
            );

            try (OutputStream out = response.getOutputStream()) {
                new ObjectMapper().writeValue(out, apiError);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
