package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.Exceptions.PostNotFoundException;
import com.youssef.socialnetwork.Exceptions.UserNotFoundException;
import com.youssef.socialnetwork.dto.PagedResponse;
import com.youssef.socialnetwork.dto.ReportedPostDTO;
import com.youssef.socialnetwork.dto.TopAuthorDTO;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 🔒 Ban user
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setEnabled(false);
        userRepository.save(user);
    }

    // 🗑️ Delete user
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // 🗑️ Delete post
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    // 📊 User stats
    public java.util.Map<String, Long> getUserStats() {
        return java.util.Map.of(
                "totalUsers", userRepository.countByDeletedAtIsNull(),
                "deletedUsers", userRepository.countByDeletedAtIsNotNull(),
                "usersCreatedToday", userRepository.countUsersCreatedToday(),
                "usersDeletedToday", userRepository.countUsersDeletedToday()
        );
    }

    // 📊 Post stats
    public java.util.Map<String, Long> getPostStats() {
        return java.util.Map.of(
                "totalPosts", postRepository.countByDeletedAtIsNull(),
                "deletedPosts", postRepository.countByDeletedAtIsNotNull(),
                "postsCreatedToday", postRepository.countPostsCreatedToday(),
                "postsDeletedToday", postRepository.countPostsDeletedToday()
        );
    }

    // 🏆 Top authors with pagination + sorting
    public PagedResponse<TopAuthorDTO> getTopAuthors(int page, int size, String sortBy, String direction) {
        var pageable = PageRequest.of(page, size);
        var authors = postRepository.findTopAuthors(pageable);

        List<TopAuthorDTO> content = authors.stream()
                .map(u -> new TopAuthorDTO(
                        u.getId(),
                        u.getUsername(),
                        postRepository.findAllByAuthor(u).size()
                ))
                .collect(Collectors.toList());

        Comparator<TopAuthorDTO> comparator = Comparator.comparing(TopAuthorDTO::getPostsCount);
        if ("authorName".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(TopAuthorDTO::getAuthorName);
        }
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        content = content.stream().sorted(comparator).collect(Collectors.toList());

        return new PagedResponse<>(page, size, content);
    }

    // 🚨 Most reported posts with pagination + sorting
    public PagedResponse<ReportedPostDTO> getMostReportedPosts(int page, int size, String sortBy, String direction) {
        var pageable = PageRequest.of(page, size);
        var posts = postRepository.findMostReportedPosts(pageable);

        List<ReportedPostDTO> content = posts.stream()
                .map(p -> new ReportedPostDTO(
                        p.getId(),
                        p.getAuthor().getUsername(),
                        p.getReports().size(),
                        p.getContent()
                ))
                .collect(Collectors.toList());

        Comparator<ReportedPostDTO> comparator = Comparator.comparing(ReportedPostDTO::getReportsCount);
        if ("authorName".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(ReportedPostDTO::getAuthorName);
        }
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        content = content.stream().sorted(comparator).collect(Collectors.toList());

        return new PagedResponse<>(page, size, content);
    }
}
