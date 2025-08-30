package com.youssef.socialnetwork.Testing.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ❌ 1) User مش عامل Login → 401 Unauthorized
    @Test
    void testUnauthorizedUserGets401() throws Exception {
        mockMvc.perform(get("/api/admin/user-stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource"));
    }

    // ❌ 2) User عادي (ROLE_USER) → 403 Forbidden
    @Test
    @WithMockUser(username = "youssef", roles = {"USER"})
    void testForbiddenForNonAdminUser() throws Exception {
        mockMvc.perform(get("/api/admin/user-stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Access Denied"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"));
    }

    // ✅ 3) User Admin (ROLE_ADMIN) → 200 OK
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminAccessAllowed() throws Exception {
        mockMvc.perform(get("/api/admin/user-stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // 👈 هنا بس بنتأكد إنه دخل
    }
}
