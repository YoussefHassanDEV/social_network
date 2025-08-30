package com.youssef.socialnetwork.Testing.Repository;

import com.youssef.socialnetwork.Enums.ReportCategory;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.Report;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.ReportRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;
    private Report report;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("reporter");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        post = new Post();
        post.setAuthor(user);
        post.setContent("Inappropriate post");
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        report = new Report();
        report.setPost(post);
        report.setReporter(user);
        report.setCategory(ReportCategory.SPAM); // ✅ بدل reason
        report.setCreatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    @Test
    void testSaveReport() {
        Report saved = reportRepository.findById(report.getId()).orElseThrow();
        assertEquals(ReportCategory.SPAM, saved.getCategory());  // ✅ Enum بدل string
        assertEquals(post.getId(), saved.getPost().getId());
        assertEquals(user.getId(), saved.getReporter().getId());
    }
}
