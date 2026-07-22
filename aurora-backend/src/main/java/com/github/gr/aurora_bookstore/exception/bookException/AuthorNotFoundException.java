package com.github.gr.aurora_bookstore.exception.bookException;

public class AuthorNotFoundException extends ResourceNotFoundException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}
