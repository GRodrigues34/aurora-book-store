package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.exception.bookException.ResourceNotFoundException;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import com.github.gr.aurora_bookstore.service.AuthService;
import com.github.gr.aurora_bookstore.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CartService cartService;

    private User validUser;
    private String token;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(10L);
        validUser.setUsername("testuser");
        validUser.setEmail("user@test.com");
        validUser.setRole(UserRole.USER);

        token = authService.generateToken(validUser);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(validUser));
    }

    @Test
    void shouldReturnFormattedErrorResponseWhenResourceNotFoundExceptionThrown() throws Exception {
        // Arrange
        when(cartService.getCart(any())).thenThrow(new ResourceNotFoundException("Cart not found for user id: 10"));

        // Act & Assert
        mockMvc.perform(get("/api/carts")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cart not found for user id: 10"))
                .andExpect(jsonPath("$.path").value("/api/carts"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
