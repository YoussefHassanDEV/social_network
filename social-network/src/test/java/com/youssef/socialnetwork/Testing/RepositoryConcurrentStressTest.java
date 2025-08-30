package com.youssef.socialnetwork.Testing;

import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import com.youssef.socialnetwork.Testing.Util.TestDataFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryConcurrentStressTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private static boolean dataInitialized = false;

    @BeforeAll
    static void setupOnce(@Autowired UserRepository userRepo,
                          @Autowired PostRepository postRepo) {
        if (!dataInitialized) {
            System.out.println("Cleaning old test data...");
            postRepo.deleteAll();  // delete posts first because they reference users
            userRepo.deleteAll();  // delete users

            System.out.println("Generating heavy test data...");
            List<User> users = TestDataFactory.createUsers(userRepo, 200000); // 20k users
            TestDataFactory.createPosts(postRepo, users, 500000); // 50k posts

            dataInitialized = true;
        }
    }
    @Test
    void testConcurrentTopAuthors() throws InterruptedException {
        int threads = 20; // 20 concurrent threads
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try {
            List<Callable<Long>> tasks = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                tasks.add(() -> {
                    long start = System.currentTimeMillis();
                    postRepository.findTopAuthors(PageRequest.of(0, 10));
                    return System.currentTimeMillis() - start;
                });
            }

            List<Future<Long>> results = executor.invokeAll(tasks);

            for (Future<Long> result : results) {
                try {
                    System.out.println("Top Authors Query took " + result.get() + " ms");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    @Test
    void testConcurrentSmartFeed() throws InterruptedException {
        List<User> sampleUsers = userRepository.findAll().subList(0, 100);
        int threads = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Callable<Long>> tasks = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                tasks.add(() -> {
                    long start = System.currentTimeMillis();
                    postRepository.findSmartFeed(sampleUsers, "en", "EG", PageRequest.of(0, 20));
                    return System.currentTimeMillis() - start;
                });
            }

            List<Future<Long>> results = executor.invokeAll(tasks);

            for (Future<Long> result : results) {
                try {
                    System.out.println("Smart Feed Query took " + result.get() + " ms");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
