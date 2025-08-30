package com.youssef.socialnetwork.Testing;

import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.ReportRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import com.youssef.socialnetwork.Testing.Util.TestDataFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryPerformanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportRepository reportRepository;

    private static final int USERS_COUNT = 20000;
    private static final int POSTS_COUNT = 50000;
    private static final int REPORTS_COUNT = 100000;

    private static boolean dataInitialized = false;

    @BeforeAll
    static void setupOnce(@Autowired UserRepository userRepo,
                          @Autowired PostRepository postRepo,
                          @Autowired ReportRepository reportRepo) {
        if (!dataInitialized) {
            System.out.println("Cleaning old test data...");
            reportRepo.deleteAll(); // delete reports first
            postRepo.deleteAll();   // delete posts
            userRepo.deleteAll();   // delete users

            System.out.println("Generating heavy test data...");
            List<User> users = TestDataFactory.createUsers(userRepo, USERS_COUNT);
            List<Post> posts = TestDataFactory.createPosts(postRepo, users, POSTS_COUNT);
            TestDataFactory.createReports(reportRepo, posts, users, REPORTS_COUNT);

            dataInitialized = true;
        }
    }

    private long measureTime(String label, Runnable task) {
        long start = System.currentTimeMillis();
        task.run();
        long end = System.currentTimeMillis();
        long duration = (end - start);
        System.out.println("⏱ " + label + " took " + duration + " ms");
        return duration;
    }

    @Test
    void testPerformanceTopAuthors() {
        long duration = measureTime("Top Authors Query", () ->
                postRepository.findTopAuthors(PageRequest.of(0, 10))
        );
        assertTrue(duration < 500, "Top Authors query is too slow!");
    }

    @Test
    void testPerformanceMostReportedPosts() {
        long duration = measureTime("Most Reported Posts Query", () ->
                postRepository.findMostReportedPosts(PageRequest.of(0, 10))
        );
        assertTrue(duration < 500, "Most Reported Posts query is too slow!");
    }

    @Test
    void testPerformanceSmartFeed() {
        List<User> sampleUsers = userRepository.findAll().subList(0, 100);
        long duration = measureTime("Smart Feed Query", () ->
                postRepository.findSmartFeed(sampleUsers, "en", "EG", PageRequest.of(0, 20))
        );
        assertTrue(duration < 500, "Smart Feed query is too slow!");
    }
}
