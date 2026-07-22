package com.github.gr.aurora_bookstore.exception.handler;

import com.github.gr.aurora_bookstore.exception.aiException.AiCommunicationException;
import com.github.gr.aurora_bookstore.exception.aiException.InvalidMessageException;
import com.github.gr.aurora_bookstore.exception.bookException.ResourceNotFoundException;
import com.github.gr.aurora_bookstore.exception.cartException.CartItemNotFoundException;
import com.github.gr.aurora_bookstore.exception.cartException.CartOwnershipException;
import com.github.gr.aurora_bookstore.exception.orderException.EmptyCartException;
import com.github.gr.aurora_bookstore.exception.orderException.InsufficientStockException;
import com.github.gr.aurora_bookstore.exception.orderException.OrderNotFoundException;
import com.github.gr.aurora_bookstore.exception.response.ErrorResponse;
import com.github.gr.aurora_bookstore.exception.userException.InvalidEmailException;
import com.github.gr.aurora_bookstore.exception.userException.InvalidPasswordException;
import com.github.gr.aurora_bookstore.exception.userException.UserAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final BookExceptionHandler bookExceptionHandler;
    private final UserExceptionHandler userExceptionHandler;
    private final CartExceptionHandler cartExceptionHandler;
    private final OrderExceptionHandler orderExceptionHandler;
    private final AiExceptionHandler aiExceptionHandler;

    // --- Book / Catalog Exceptions ---
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return bookExceptionHandler.handleResourceNotFound(ex, request.getRequestURI());
    }

    // --- User / Auth Exceptions ---
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmail(InvalidEmailException ex, HttpServletRequest request) {
        return userExceptionHandler.handleInvalidEmail(ex, request.getRequestURI());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest request) {
        return userExceptionHandler.handleInvalidPassword(ex, request.getRequestURI());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        return userExceptionHandler.handleUserAlreadyExists(ex, request.getRequestURI());
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex, HttpServletRequest request) {
        return userExceptionHandler.handleAuthenticationException(ex, request.getRequestURI());
    }

    // --- Cart Exceptions ---
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException ex, HttpServletRequest request) {
        return cartExceptionHandler.handleCartItemNotFound(ex, request.getRequestURI());
    }

    @ExceptionHandler(CartOwnershipException.class)
    public ResponseEntity<ErrorResponse> handleCartOwnership(CartOwnershipException ex, HttpServletRequest request) {
        return cartExceptionHandler.handleCartOwnership(ex, request.getRequestURI());
    }

    // --- Order Exceptions ---
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        return orderExceptionHandler.handleInsufficientStock(ex, request.getRequestURI());
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> handleEmptyCart(EmptyCartException ex, HttpServletRequest request) {
        return orderExceptionHandler.handleEmptyCart(ex, request.getRequestURI());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        return orderExceptionHandler.handleOrderNotFound(ex, request.getRequestURI());
    }

    // --- AI Exceptions ---
    @ExceptionHandler(AiCommunicationException.class)
    public ResponseEntity<ErrorResponse> handleAiCommunication(AiCommunicationException ex, HttpServletRequest request) {
        return aiExceptionHandler.handleAiCommunication(ex, request.getRequestURI());
    }

    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMessage(InvalidMessageException ex, HttpServletRequest request) {
        return aiExceptionHandler.handleInvalidMessage(ex, request.getRequestURI());
    }

    // --- Validation Exceptions ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // --- Generic Exception Handler ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
