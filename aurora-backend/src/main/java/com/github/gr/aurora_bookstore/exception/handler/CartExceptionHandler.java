package com.github.gr.aurora_bookstore.exception.handler;

import com.github.gr.aurora_bookstore.exception.cartException.CartItemNotFoundException;
import com.github.gr.aurora_bookstore.exception.cartException.CartOwnershipException;
import com.github.gr.aurora_bookstore.exception.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CartExceptionHandler {

    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    public ResponseEntity<ErrorResponse> handleCartOwnership(CartOwnershipException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
