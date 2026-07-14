package com.github.gr.aurora_bookstore.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AuthService {

    @Autowired
    UserService userService;


    private final String secret = "conformidade";


    public UserReadDTO register(UserRegisterDTO registerDto){
        if(userService.existsByEmail(registerDto.getEmail())){
            return null;
        }else{
            String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.getPassword());
            registerDto.setPassword(encryptedPassword);
            return userService.create(registerDto);
        }
    }

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-service")
                    .withSubject(user.getEmail())
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
            return token;
        }catch (JWTCreationException exception){
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateTokenAndRetrieveSubject(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-service")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException exception){
            return "";
        }
    }

    private Instant getExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("UTC"));
    }
}
