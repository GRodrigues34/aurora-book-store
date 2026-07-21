package com.github.gr.aurora_bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gr.aurora_bookstore.dto.cartDto.CartItemInsertDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerIntegrationTest {

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
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    void getCart_WithoutToken_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCart_WithValidToken_ShouldReturn200() throws Exception {
        when(cartService.getCart(10L)).thenReturn(new CartReadDTO());

        mockMvc.perform(get("/api/carts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void insertItem_WithValidToken_ShouldReturn201() throws Exception {
        CartItemInsertDTO dto = new CartItemInsertDTO(1L, 2);
        when(cartService.insertItem(eq(10L), any(CartItemInsertDTO.class))).thenReturn(new CartReadDTO());

        mockMvc.perform(post("/api/carts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteItem_WithValidToken_ShouldReturn200() throws Exception {
        when(cartService.deleteItem(10L, 1L)).thenReturn(new CartReadDTO());

        mockMvc.perform(delete("/api/carts/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
