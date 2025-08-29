package com.youssef.socialnetwork.auth.service;

import com.youssef.socialnetwork.Enums.Role;
import com.youssef.socialnetwork.dto.AuthResponse;
import com.youssef.socialnetwork.dto.LoginRequest;
import com.youssef.socialnetwork.dto.RegisterRequest;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthResponse register(RegisterRequest req) {
        if (users.findByUsername(req.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (users.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        var user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(encoder.encode(req.password()))
                .role(Role.USER)
                .enabled(true)
                .build();
        users.save(user);
        var token = jwt.generateToken(user);
        return AuthResponse.bearer(token, jwt.getExpirationSeconds());
    }

    public AuthResponse login(LoginRequest req) {
        var auth = new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());
        authManager.authenticate(auth);
        var user = users.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var token = jwt.generateToken(user);
        return AuthResponse.bearer(token, jwt.getExpirationSeconds());
    }
}
