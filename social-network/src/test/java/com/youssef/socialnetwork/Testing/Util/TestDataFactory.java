package com.youssef.socialnetwork.Testing.Util;

import com.youssef.socialnetwork.Enums.ReportCategory;
import com.youssef.socialnetwork.Enums.Role;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.Report;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.ReportRepository;
import com.youssef.socialnetwork.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataFactory {

    private static final Random random = new Random();

    // ✅ Create dummy users
    public static List<User> createUsers(UserRepository userRepository, int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUsername("user_" + i);
            user.setEmail("user_" + i + "@example.com");
            user.setPassword("password123"); // dummy password
            user.setAge(random.nextInt(50) + 18); // random age between 18-68
            user.setLanguage(i % 2 == 0 ? "en" : "ar");
            user.setCountry(i % 3 == 0 ? "EG" : "US");
            user.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            user.setEnabled(true);
            user.setRole(Role.USER); // default role
            users.add(user);
        }
        return userRepository.saveAll(users);
    }

    // ✅ Create dummy posts
    public static List<Post> createPosts(PostRepository postRepository, List<User> users, int count) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User author = users.get(random.nextInt(users.size()));
            Post post = new Post();
            post.setAuthor(author);
            post.setContent("Post content " + i);
            post.setCreatedAt(LocalDateTime.now().minusHours(random.nextInt(100)));
            posts.add(post);
        }
        return postRepository.saveAll(posts);
    }

    // ✅ Create dummy reports
    public static void createReports(ReportRepository reportRepository, List<Post> posts, List<User> users, int count) {
        List<Report> reports = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Report report = new Report();
            report.setPost(posts.get(random.nextInt(posts.size())));
            report.setReporter(users.get(random.nextInt(users.size())));
            report.setCategory(ReportCategory.SPAM); // fixed category
            report.setCreatedAt(LocalDateTime.now().minusMinutes(random.nextInt(500)));
            reports.add(report);
        }
        reportRepository.saveAll(reports);
    }
}
