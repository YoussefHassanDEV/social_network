package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.Report;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.ReportRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public Map<String, Long> getUserStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countByDeletedAtIsNull());
        stats.put("deletedUsers", userRepository.countByDeletedAtIsNotNull());
        stats.put("usersCreatedToday", userRepository.countUsersCreatedToday());
        stats.put("usersDeletedToday", userRepository.countUsersDeletedToday());
        return stats;
    }

    public Map<String, Long> getPostStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalPosts", postRepository.countByDeletedAtIsNull());
        stats.put("deletedPosts", postRepository.countByDeletedAtIsNotNull());
        stats.put("postsCreatedToday", postRepository.countPostsCreatedToday());
        stats.put("postsDeletedToday", postRepository.countPostsDeletedToday());
        return stats;
    }
}

