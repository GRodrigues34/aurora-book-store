package com.github.gr.aurora_bookstore.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.gr.aurora_bookstore.dto.userDto.LoginDTO;
import com.github.gr.aurora_bookstore.dto.userDto.LoginResponseDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.exception.userException.EmailNotVerifiedException;
import com.github.gr.aurora_bookstore.exception.userException.InvalidVerificationTokenException;
import com.github.gr.aurora_bookstore.exception.userException.UserAlreadyExistsException;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.AuthProvider;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmailService emailService;

    @Autowired
    GoogleAuthService googleAuthService;

    @Value("${api.security.token.secret}")
    private String secret;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserReadDTO register(UserRegisterDTO registerDto) {
        if (userService.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + registerDto.getEmail());
        }

        String encryptedPassword = passwordEncoder.encode(registerDto.getPassword());
        registerDto.setPassword(encryptedPassword);
        registerDto.setRole(UserRole.USER);
        UserReadDTO userReadDto = userService.create(registerDto);
        User user = userService.getEntityById(userReadDto.getId());

        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerified(false);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userService.saveEntity(user);

        cartService.create(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationToken);
        return userReadDto;
    }

    public LoginResponseDTO login(LoginDTO loginDto) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(
                loginDto.login(),
                loginDto.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        User user = (User) auth.getPrincipal();

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email address before signing in.");
        }

        String token = generateToken(user);
        return new LoginResponseDTO(token);
    }

    @Transactional
    public void verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidVerificationTokenException("Verification token is required");
        }
        User user = userService.findEntityByVerificationToken(token)
                .orElseThrow(() -> new InvalidVerificationTokenException("Invalid verification token"));

        if (user.getVerificationTokenExpiresAt() != null
                && user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidVerificationTokenException("Verification token has expired");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        userService.saveEntity(user);
    }

    @Transactional
    public void resendVerification(String email) {
        User user = userService.findEntityByEmail(email).orElse(null);
        if (user == null || user.isEmailVerified()) {
            return;
        }
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userService.saveEntity(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationToken);
    }

    @Transactional
    public LoginResponseDTO loginWithGoogle(String credential) {
        GoogleAuthService.GoogleUserInfo googleUser = googleAuthService.verifyIdToken(credential);

        User user = userService.findEntityByEmail(googleUser.email()).orElse(null);
        if (user == null) {
            User newUser = User.builder()
                    .username(googleUser.name())
                    .email(googleUser.email())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(UserRole.USER)
                    .emailVerified(true)
                    .authProvider(AuthProvider.GOOGLE)
                    .build();
            user = userService.saveEntity(newUser);
            cartService.create(user);
        } else if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiresAt(null);
            user = userService.saveEntity(user);
        }

        String token = generateToken(user);
        return new LoginResponseDTO(token);
    }

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-service")
                    .withSubject(user.getEmail())
                    .withClaim("role", (user.getRole() != null) ? user.getRole().name() : "USER")
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateTokenAndRetrieveSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-service")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant getExpirationDate() {
        return Instant.now().plusSeconds(7200);
    }
}
