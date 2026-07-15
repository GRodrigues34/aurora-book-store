package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "secret", "test-secret-key-that-is-very-long-to-be-secure");
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldReturnNull() {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("test@test.com");

        when(userService.existsByEmail("test@test.com")).thenReturn(true);

        // Act
        UserReadDTO result = authService.register(dto);

        // Assert
        assertNull(result);
        verify(userService, never()).create(any());
    }

    @Test
    void register_WhenEmailIsNew_ShouldEncryptPasswordAndReturnUser() {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("user");
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        dto.setRole(UserRole.USER);

        UserReadDTO expectedUser = new UserReadDTO();
        expectedUser.setId(1L);
        expectedUser.setUsername("user");
        expectedUser.setEmail("test@test.com");
        expectedUser.setRole("USER");

        when(userService.existsByEmail("test@test.com")).thenReturn(false);
        when(userService.create(any(UserRegisterDTO.class))).thenReturn(expectedUser);

        // Act
        UserReadDTO result = authService.register(dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getEmail(), result.getEmail());

        // Note: bcrypt usually generates hash starting with $2a$
        assertTrue(dto.getPassword().startsWith("$2a$"));
        verify(userService, times(1)).create(any());
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        // Arrange
        User user = new User();
        user.setEmail("auth@test.com");

        // Act
        String token = authService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateTokenAndRetrieveSubject_WithValidToken_ShouldReturnEmail() {
        // Arrange
        User user = new User();
        user.setEmail("validate@test.com");
        String token = authService.generateToken(user);

        // Act
        String subject = authService.validateTokenAndRetrieveSubject(token);

        // Assert
        assertEquals("validate@test.com", subject);
    }

    @Test
    void validateTokenAndRetrieveSubject_WithInvalidToken_ShouldReturnEmptyString() {
        // Arrange
        String token = "invalid.jwt.token";

        // Act
        String subject = authService.validateTokenAndRetrieveSubject(token);

        // Assert
        assertEquals("", subject);
    }
}
