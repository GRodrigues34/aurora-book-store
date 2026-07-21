package com.github.gr.aurora_bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gr.aurora_bookstore.dto.aiDto.ChatRequest;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import com.github.gr.aurora_bookstore.service.AiService;
import com.github.gr.aurora_bookstore.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AiService aiService;

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
    void chat_WithoutToken_ShouldReturn403() throws Exception {
        ChatRequest request = new ChatRequest("Hello");
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void chat_WithValidToken_ShouldReturn200() throws Exception {
        ChatRequest request = new ChatRequest("Hello");
        when(aiService.receiveUserMessage(any(ChatRequest.class), eq(10L))).thenReturn(Flux.just("Hi"));

        mockMvc.perform(post("/api/chat")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getChatHistory_WithValidToken_ShouldReturn200() throws Exception {
        when(aiService.chatMessagesToDto(10L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/chat")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
