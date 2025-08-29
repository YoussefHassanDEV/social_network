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

    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = CURRENT_DATE")
    long countUsersCreatedToday();

    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.deletedAt) = CURRENT_DATE")
    long countUsersDeletedToday();
}
