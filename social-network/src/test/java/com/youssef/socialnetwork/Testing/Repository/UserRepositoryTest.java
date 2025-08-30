package com.youssef.socialnetwork.Testing.Repository;

import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User activeUser, deletedUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setUsername("active");
        activeUser.setCreatedAt(LocalDateTime.now());
        userRepository.save(activeUser);

        deletedUser = new User();
        deletedUser.setUsername("deleted");
        deletedUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        deletedUser.setDeletedAt(LocalDateTime.now());
        userRepository.save(deletedUser);
    }

    @Test
    void testCountByDeletedAtIsNull() {
        long count = userRepository.countByDeletedAtIsNull();
        assertEquals(1, count);
    }

    @Test
    void testCountByDeletedAtIsNotNull() {
        long count = userRepository.countByDeletedAtIsNotNull();
        assertEquals(1, count);
    }

    @Test
    void testUsersCreatedToday() {
        long count = userRepository.countUsersCreatedToday();
        assertEquals(1, count);
    }

    @Test
    void testUsersDeletedToday() {
        long count = userRepository.countUsersDeletedToday();
        assertEquals(1, count);
    }
}
