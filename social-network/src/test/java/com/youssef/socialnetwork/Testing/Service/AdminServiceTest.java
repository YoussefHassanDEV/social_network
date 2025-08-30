package com.youssef.socialnetwork.Testing.Service;

import com.youssef.socialnetwork.Exceptions.PostNotFoundException;
import com.youssef.socialnetwork.Exceptions.UserNotFoundException;
import com.youssef.socialnetwork.dto.PagedResponse;
import com.youssef.socialnetwork.dto.ReportedPostDTO;
import com.youssef.socialnetwork.dto.TopAuthorDTO;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import com.youssef.socialnetwork.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private AdminService adminService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("youssef");
        user.setEnabled(true);

        post = new Post();
        post.setId(1L);
        post.setAuthor(user);
        post.setContent("Test post");
    }

    @Test
    void testBanUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.banUser(1L);

        assertFalse(user.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void testBanUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.banUser(99L));
    }

    @Test
    void testDeletePostSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        adminService.deletePost(1L);

        assertNotNull(post.getDeletedAt());
        verify(postRepository).save(post);
    }

    @Test
    void testDeletePostNotFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> adminService.deletePost(99L));
    }

    @Test
    void testGetUserStats() {
        when(userRepository.countByDeletedAtIsNull()).thenReturn(10L);
        when(userRepository.countByDeletedAtIsNotNull()).thenReturn(2L);
        when(userRepository.countUsersCreatedToday()).thenReturn(1L);
        when(userRepository.countUsersDeletedToday()).thenReturn(0L);

        var stats = adminService.getUserStats();

        assertEquals(10L, stats.get("totalUsers"));
        assertEquals(2L, stats.get("deletedUsers"));
        assertEquals(1L, stats.get("usersCreatedToday"));
    }

    @Test
    void testGetTopAuthors() {
        when(postRepository.findTopAuthors(PageRequest.of(0, 2))).thenReturn(List.of(user));
        when(postRepository.findAllByAuthor(user)).thenReturn(List.of(post, post));

        PagedResponse<TopAuthorDTO> response = adminService.getTopAuthors(0, 2, "postsCount", "desc");

        assertEquals(1, response.getData().size());
        assertEquals("youssef", response.getData().get(0).getAuthorName());
        assertEquals(2, response.getData().get(0).getPostsCount());
    }

    @Test
    void testGetMostReportedPosts() {
        post.setReports(List.of()); // simulate empty reports
        when(postRepository.findMostReportedPosts(PageRequest.of(0, 2))).thenReturn(List.of(post));

        PagedResponse<ReportedPostDTO> response = adminService.getMostReportedPosts(0, 2, "reportsCount", "desc");

        assertEquals(1, response.getData().size());
        assertEquals("youssef", response.getData().get(0).getAuthorName());
    }
}
