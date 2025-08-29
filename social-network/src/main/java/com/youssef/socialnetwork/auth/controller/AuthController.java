package com.youssef.socialnetwork.auth.controller;

import com.youssef.socialnetwork.Exceptions.TooManyRequestsException;
import com.youssef.socialnetwork.auth.service.AuthService;
import com.youssef.socialnetwork.auth.service.JwtService;
import com.youssef.socialnetwork.config.Redis.RateLimiterService;
import com.youssef.socialnetwork.dto.AuthResponse;
import com.youssef.socialnetwork.dto.LoginRequest;
import com.youssef.socialnetwork.dto.RegisterRequest;
import com.youssef.socialnetwork.service.JwtBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;
    private final JwtBlacklistService jwtBlacklistService;
    private final JwtService jwtService;
    private final RateLimiterService rateLimiter; // ✅

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(auth.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest http) {
        // Build a bucket key: route + email + ip
        String ip = resolveIp(http);
        String email = request.getEmail() == null ? "unknown" : request.getEmail().toLowerCase();
        String bucketKey = "rl:login:" + email + ":" + ip;

        // increment and check
        long attempts = rateLimiter.hitLogin(bucketKey);
        if (rateLimiter.loginLimited(bucketKey)) {
            throw new TooManyRequestsException("Too many login attempts. Please try again later.");
        }

        // Proceed with actual login
        return ResponseEntity.ok(auth.login(request));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long exp = jwtService.getExpirationSeconds();
            jwtBlacklistService.blacklistToken(token, exp);
        }
        return ResponseEntity.ok().build();
    }

    private String resolveIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // XFF can be a list; take first
            int comma = forwarded.indexOf(',');
            return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
        }
        return req.getRemoteAddr();
    }
}
