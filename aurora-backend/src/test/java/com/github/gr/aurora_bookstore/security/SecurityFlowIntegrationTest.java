package com.github.gr.aurora_bookstore.security;

import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import com.github.gr.aurora_bookstore.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    private User adminUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(UserRole.ADMIN);

        validToken = authService.generateToken(adminUser);
    }

    @Test
    void accessProtectedEndpoint_WithoutToken_ShouldReturn403() throws Exception {
        // Arrange
        // (No arrange needed for this case)

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedEndpoint_WithInvalidToken_ShouldReturn403() throws Exception {
        // Arrange
        // (No arrange needed for this case)

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer invalid.fake.token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedEndpoint_WithValidToken_ShouldBeAuthorized() throws Exception {
        // Arrange
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
