package com.youssef.socialnetwork.Testing.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youssef.socialnetwork.dto.PagedResponse;
import com.youssef.socialnetwork.dto.ReportedPostDTO;
import com.youssef.socialnetwork.dto.TopAuthorDTO;
import com.youssef.socialnetwork.service.AdminService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AdminService adminService() {
            return Mockito.mock(AdminService.class);
        }
    }

    // ✅ GET /user-stats
    @Test
    void testGetUserStats() throws Exception {
        Mockito.when(adminService.getUserStats()).thenReturn(
                Map.of("totalUsers", 10L, "deletedUsers", 2L, "usersCreatedToday", 1L, "usersDeletedToday", 0L)
        );

        mockMvc.perform(get("/api/admin/user-stats").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.deletedUsers").value(2));
    }

    // ✅ GET /post-stats
    @Test
    void testGetPostStats() throws Exception {
        Mockito.when(adminService.getPostStats()).thenReturn(
                Map.of("totalPosts", 20L, "deletedPosts", 5L, "postsCreatedToday", 2L, "postsDeletedToday", 1L)
        );

        mockMvc.perform(get("/api/admin/post-stats").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPosts").value(20))
                .andExpect(jsonPath("$.deletedPosts").value(5));
    }

    // ✅ POST /ban-user/{userId}
    @Test
    void testBanUser() throws Exception {
        mockMvc.perform(post("/api/admin/ban-user/1"))
                .andExpect(status().isOk());

        Mockito.verify(adminService).banUser(1L);
    }

    // ✅ DELETE /delete-user/{userId}
    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/admin/delete-user/1"))
                .andExpect(status().isOk());

        Mockito.verify(adminService).deleteUser(1L);
    }

    // ✅ DELETE /delete-post/{postId}
    @Test
    void testDeletePost() throws Exception {
        mockMvc.perform(delete("/api/admin/delete-post/1"))
                .andExpect(status().isOk());

        Mockito.verify(adminService).deletePost(1L);
    }

    // ✅ GET /top-authors
    @Test
    void testGetTopAuthors() throws Exception {
        PagedResponse<TopAuthorDTO> response = new PagedResponse<>(
                0, 2,
                List.of(
                        new TopAuthorDTO(1L, "youssef", 5),
                        new TopAuthorDTO(2L, "ahmed", 3)
                )
        );

        Mockito.when(adminService.getTopAuthors(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/top-authors?page=0&size=2&sortBy=postsCount&direction=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].authorName").value("youssef"))
                .andExpect(jsonPath("$.data[0].postsCount").value(5));
    }

    // ✅ GET /most-reported-posts
    @Test
    void testGetMostReportedPosts() throws Exception {
        PagedResponse<ReportedPostDTO> response = new PagedResponse<>(
                0, 2,
                List.of(
                        new ReportedPostDTO(1L, "youssef", 4, "Bad post"),
                        new ReportedPostDTO(2L, "ahmed", 2, "Spam post")
                )
        );

        Mockito.when(adminService.getMostReportedPosts(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/most-reported-posts?page=0&size=2&sortBy=reportsCount&direction=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].authorName").value("youssef"))
                .andExpect(jsonPath("$.data[0].reportsCount").value(4));
    }
}
