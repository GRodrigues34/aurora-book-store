package com.github.gr.aurora_bookstore.exception.handler;

import com.github.gr.aurora_bookstore.exception.orderException.EmptyCartException;
import com.github.gr.aurora_bookstore.exception.response.ErrorResponse;
import com.github.gr.aurora_bookstore.exception.orderException.InsufficientStockException;
import com.github.gr.aurora_bookstore.exception.orderException.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderExceptionHandler {

    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    public ResponseEntity<ErrorResponse> handleEmptyCart(EmptyCartException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
