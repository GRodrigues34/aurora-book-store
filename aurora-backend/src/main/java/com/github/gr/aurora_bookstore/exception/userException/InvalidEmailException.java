package com.github.gr.aurora_bookstore.exception.userException;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
