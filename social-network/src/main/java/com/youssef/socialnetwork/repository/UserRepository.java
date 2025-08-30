package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    long countByDeletedAtIsNull();
    long countByDeletedAtIsNotNull();

    // ✅ Users created today
    @Query(value = "SELECT COUNT(*) FROM users WHERE CAST(created_at AS DATE) = CURRENT_DATE AND deleted_at IS NULL", nativeQuery = true)
    long countUsersCreatedToday();

    // ✅ Users deleted today
    @Query(value = "SELECT COUNT(*) FROM users WHERE CAST(deleted_at AS DATE) = CURRENT_DATE", nativeQuery = true)
    long countUsersDeletedToday();
}
