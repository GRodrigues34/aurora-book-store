package com.github.gr.aurora_bookstore.exception.handler;

import com.github.gr.aurora_bookstore.exception.response.ErrorResponse;
import com.github.gr.aurora_bookstore.exception.userException.EmailNotVerifiedException;
import com.github.gr.aurora_bookstore.exception.userException.GoogleAuthException;
import com.github.gr.aurora_bookstore.exception.userException.InvalidEmailException;
import com.github.gr.aurora_bookstore.exception.userException.InvalidPasswordException;
import com.github.gr.aurora_bookstore.exception.userException.InvalidVerificationTokenException;
import com.github.gr.aurora_bookstore.exception.userException.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserExceptionHandler {

    public ResponseEntity<ErrorResponse> handleInvalidEmail(InvalidEmailException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    public ResponseEntity<ErrorResponse> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    public ResponseEntity<ErrorResponse> handleEmailNotVerified(EmailNotVerifiedException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Email Not Verified",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    public ResponseEntity<ErrorResponse> handleInvalidVerificationToken(InvalidVerificationTokenException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    public ResponseEntity<ErrorResponse> handleGoogleAuth(GoogleAuthException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
