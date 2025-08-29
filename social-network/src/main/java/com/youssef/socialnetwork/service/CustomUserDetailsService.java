package com.youssef.socialnetwork.service; // <- adjust to your package

import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    @Autowired
    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    /**
     * Accepts either username OR email.
     * Returns a Spring Security UserDetails whose username is the application's username (not email).
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<User> maybeUser = users.findByUsername(usernameOrEmail);
        if (maybeUser.isEmpty()) {
            maybeUser = users.findByEmail(usernameOrEmail);
        }

        User u = maybeUser.orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        // Build Spring Security UserDetails using the app username, not the email.
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole() != null ? u.getRole().name() : "USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
