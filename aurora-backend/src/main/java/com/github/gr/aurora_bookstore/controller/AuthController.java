package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.userDto.AuthDTO;
import com.github.gr.aurora_bookstore.dto.userDto.LoginResponseDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import com.github.gr.aurora_bookstore.service.AuthService;
import com.github.gr.aurora_bookstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthDTO authDto){
        var usernamePassword = new UsernamePasswordAuthenticationToken(authDto.login(), authDto.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = authService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserReadDTO> register(@RequestBody @Valid UserRegisterDTO registerDto){
        UserReadDTO registeredUser =  authService.register(registerDto);
        if(registeredUser == null){
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        }
    }
}
