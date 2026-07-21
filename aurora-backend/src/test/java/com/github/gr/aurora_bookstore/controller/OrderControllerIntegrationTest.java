package com.github.gr.aurora_bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderCreateDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderReadDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import com.github.gr.aurora_bookstore.service.AuthService;
import com.github.gr.aurora_bookstore.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private OrderService orderService;

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
    void createOrder_WithoutToken_ShouldReturn403() throws Exception {
        OrderCreateDTO dto = new OrderCreateDTO();
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createOrder_WithValidToken_ShouldReturn201() throws Exception {
        OrderCreateDTO dto = new OrderCreateDTO();
        when(orderService.createOrder(eq(10L), any(OrderCreateDTO.class))).thenReturn(new OrderReadDTO());

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getOrders_WithValidToken_ShouldReturn200() throws Exception {
        when(orderService.getOrdersByUserId(10L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOrder_WithValidToken_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/orders/100")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
