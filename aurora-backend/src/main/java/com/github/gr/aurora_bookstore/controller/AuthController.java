package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.userDto.GoogleLoginDTO;
import com.github.gr.aurora_bookstore.dto.userDto.LoginDTO;
import com.github.gr.aurora_bookstore.dto.userDto.LoginResponseDTO;
import com.github.gr.aurora_bookstore.dto.userDto.MessageResponseDTO;
import com.github.gr.aurora_bookstore.dto.userDto.ResendVerificationDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO loginDto) {
        LoginResponseDTO token = authService.login(loginDto);
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/register")
    public ResponseEntity<UserReadDTO> register(@RequestBody @Valid UserRegisterDTO registerDto) {
        UserReadDTO registeredUser = authService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponseDTO> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(new MessageResponseDTO("Email verified successfully. You can now sign in."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponseDTO> resendVerification(@RequestBody @Valid ResendVerificationDTO dto) {
        authService.resendVerification(dto.email());
        return ResponseEntity.ok(new MessageResponseDTO("If an unverified account exists for this email, a new verification link has been sent."));
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> loginWithGoogle(@RequestBody @Valid GoogleLoginDTO dto) {
        return ResponseEntity.ok(authService.loginWithGoogle(dto.credential()));
    }
}
