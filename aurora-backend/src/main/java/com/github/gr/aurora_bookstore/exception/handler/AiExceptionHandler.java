package com.github.gr.aurora_bookstore.exception.handler;

import com.github.gr.aurora_bookstore.exception.response.ErrorResponse;
import com.github.gr.aurora_bookstore.exception.aiException.AiCommunicationException;
import com.github.gr.aurora_bookstore.exception.aiException.InvalidMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AiExceptionHandler {

    public ResponseEntity<ErrorResponse> handleAiCommunication(AiCommunicationException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    public ResponseEntity<ErrorResponse> handleInvalidMessage(InvalidMessageException ex, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
