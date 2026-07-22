package com.github.gr.aurora_bookstore.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.gr.aurora_bookstore.dto.userDto.LoginDTO;
import com.github.gr.aurora_bookstore.dto.userDto.LoginResponseDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.exception.userException.UserAlreadyExistsException;
import com.github.gr.aurora_bookstore.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @org.springframework.beans.factory.annotation.Value("${api.security.token.secret}")
    private String secret;

    public UserReadDTO register(UserRegisterDTO registerDto) {
        if (userService.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + registerDto.getEmail());
        } else {
            String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.getPassword());
            registerDto.setPassword(encryptedPassword);
            registerDto.setRole(com.github.gr.aurora_bookstore.model.enums.UserRole.USER);
            return userService.create(registerDto);
        }
    }

    public LoginResponseDTO login(LoginDTO loginDto) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(loginDto.login(),
                loginDto.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        String token = generateToken((User) auth.getPrincipal());
        return new LoginResponseDTO(token);
    }

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-service")
                    .withSubject(user.getEmail())
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
            return token;
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
