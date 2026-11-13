package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.userDto.LoginDTO;
import com.github.gr.aurora_bookstore.dto.userDto.LoginResponseDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.exception.userException.EmailNotVerifiedException;
import com.github.gr.aurora_bookstore.exception.userException.InvalidVerificationTokenException;
import com.github.gr.aurora_bookstore.exception.userException.UserAlreadyExistsException;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.AuthProvider;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.util.UserTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @Mock
    private GoogleAuthService googleAuthService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "secret", "test-secret-key-that-is-very-long-to-be-secure");
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowException() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("test@test.com");

        when(userService.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(dto));
        verify(userService, never()).create(any());
        verify(emailService, never()).sendVerificationEmail(any(), any(), any());
    }

    @Test
    void register_WhenEmailIsNew_ShouldEncryptPasswordSendVerificationAndReturnUser() {
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

        User userEntity = UserTestData.createValidUserUser();

        when(userService.existsByEmail("test@test.com")).thenReturn(false);
        when(userService.create(any(UserRegisterDTO.class))).thenReturn(expectedUser);
        when(userService.getEntityById(1L)).thenReturn(userEntity);
        when(userService.saveEntity(any(User.class))).thenReturn(userEntity);

        UserReadDTO result = authService.register(dto);

        assertNotNull(result);
        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getEmail(), result.getEmail());
        assertTrue(dto.getPassword().startsWith("$2a$"));
        assertFalse(userEntity.isEmailVerified());
        assertNotNull(userEntity.getVerificationToken());
        verify(userService, times(1)).create(any());
        verify(userService, times(1)).saveEntity(userEntity);
        verify(cartService, times(1)).create(userEntity);
        verify(emailService, times(1)).sendVerificationEmail(eq(userEntity.getEmail()), eq(userEntity.getUsername()), anyString());
    }

    @Test
    void login_WhenEmailNotVerified_ShouldThrowEmailNotVerifiedException() {
        User unverifiedUser = UserTestData.createValidUserUser();
        unverifiedUser.setEmailVerified(false);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(unverifiedUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        assertThrows(EmailNotVerifiedException.class,
                () -> authService.login(new LoginDTO(unverifiedUser.getEmail(), "password")));
    }

    @Test
    void verifyEmail_WithValidToken_ShouldMarkEmailVerified() {
        User user = UserTestData.createValidUserUser();
        user.setEmailVerified(false);
        user.setVerificationToken("valid-token");
        user.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(2));

        when(userService.findEntityByVerificationToken("valid-token")).thenReturn(Optional.of(user));
        when(userService.saveEntity(any(User.class))).thenReturn(user);

        authService.verifyEmail("valid-token");

        assertTrue(user.isEmailVerified());
        assertNull(user.getVerificationToken());
        assertNull(user.getVerificationTokenExpiresAt());
        verify(userService, times(1)).saveEntity(user);
    }

    @Test
    void verifyEmail_WithExpiredToken_ShouldThrowException() {
        User user = UserTestData.createValidUserUser();
        user.setEmailVerified(false);
        user.setVerificationToken("expired-token");
        user.setVerificationTokenExpiresAt(LocalDateTime.now().minusMinutes(5));

        when(userService.findEntityByVerificationToken("expired-token")).thenReturn(Optional.of(user));

        assertThrows(InvalidVerificationTokenException.class, () -> authService.verifyEmail("expired-token"));
        verify(userService, never()).saveEntity(any());
    }

    @Test
    void loginWithGoogle_WhenNewUser_ShouldCreateVerifiedUserAndReturnToken() {
        GoogleAuthService.GoogleUserInfo googleInfo =
                new GoogleAuthService.GoogleUserInfo("reader@gmail.com", "Reader", true);

        when(googleAuthService.verifyIdToken("google-id-token")).thenReturn(googleInfo);
        when(userService.findEntityByEmail("reader@gmail.com")).thenReturn(Optional.empty());
        when(userService.saveEntity(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        LoginResponseDTO response = authService.loginWithGoogle("google-id-token");

        assertNotNull(response);
        assertNotNull(response.token());
        assertEquals("reader@gmail.com", authService.validateTokenAndRetrieveSubject(response.token()));
        verify(cartService, times(1)).create(any(User.class));
        verify(userService, times(1)).saveEntity(argThat(u ->
                u.isEmailVerified() && u.getAuthProvider() == AuthProvider.GOOGLE));
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        User user = UserTestData.createValidUserUser();
        String token = authService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateTokenAndRetrieveSubject_WithValidToken_ShouldReturnEmail() {
        User user = UserTestData.createValidUserUser();
        String token = authService.generateToken(user);
        String subject = authService.validateTokenAndRetrieveSubject(token);
        assertEquals(user.getEmail(), subject);
    }

    @Test
    void validateTokenAndRetrieveSubject_WithInvalidToken_ShouldReturnEmptyString() {
        String subject = authService.validateTokenAndRetrieveSubject("invalid.jwt.token");
        assertEquals("", subject);
    }
}
