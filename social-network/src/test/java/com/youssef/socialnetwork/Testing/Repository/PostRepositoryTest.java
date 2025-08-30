package com.youssef.socialnetwork.Testing.Repository;

import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post1, post2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("youssef");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        post1 = new Post();
        post1.setAuthor(user);
        post1.setContent("First post");
        post1.setCreatedAt(LocalDateTime.now());
        postRepository.save(post1);

        post2 = new Post();
        post2.setAuthor(user);
        post2.setContent("Second post");
        post2.setCreatedAt(LocalDateTime.now().minusDays(1));
        postRepository.save(post2);
    }

    @Test
    void testCountPostsCreatedToday() {
        long count = postRepository.countPostsCreatedToday();
        assertEquals(1, count);
    }

    @Test
    void testFindAllByAuthor() {
        List<Post> posts = postRepository.findAllByAuthor(user);
        assertEquals(2, posts.size());
    }

    @Test
    void testFindTopAuthors() {
        var authors = postRepository.findTopAuthors(PageRequest.of(0, 1));
        assertEquals(1, authors.size());
        assertEquals("youssef", authors.get(0).getUsername());
    }
}
