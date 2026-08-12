package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import com.github.gr.aurora_bookstore.service.AuthService;
import com.github.gr.aurora_bookstore.service.BookImageUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookImageUploadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BookImageUploadService bookImageUploadService;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@email.com");
        adminUser.setRole(UserRole.ADMIN);

        User regularUser = new User();
        regularUser.setId(2L);
        regularUser.setUsername("user");
        regularUser.setEmail("user@email.com");
        regularUser.setRole(UserRole.USER);

        adminToken = authService.generateToken(adminUser);
        userToken = authService.generateToken(regularUser);

        when(userRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(regularUser));
    }

    @Test
    void uploadImage_AsAdmin_ShouldReturn200AndUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "fake".getBytes());
        when(bookImageUploadService.uploadImage(any())).thenReturn("http://localhost:8080/covers/clean.png");

        mockMvc.perform(multipart("/api/books/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("http://localhost:8080/covers/clean.png"));
    }

    @Test
    void uploadImage_AsUser_ShouldReturn403() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "fake".getBytes());

        mockMvc.perform(multipart("/api/books/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadImage_AsGuest_ShouldReturn403() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "fake".getBytes());

        mockMvc.perform(multipart("/api/books/upload")
                        .file(file))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadImage_WithEmptyFile_ShouldReturn400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[0]);
        when(bookImageUploadService.uploadImage(any())).thenThrow(new IllegalArgumentException("File is empty"));

        mockMvc.perform(multipart("/api/books/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File is empty"));
    }

    @Test
    void uploadImage_WithServiceException_ShouldReturn500() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "fake".getBytes());
        when(bookImageUploadService.uploadImage(any())).thenThrow(new IOException("Disk error"));

        mockMvc.perform(multipart("/api/books/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to upload file: Disk error"));
    }
}
