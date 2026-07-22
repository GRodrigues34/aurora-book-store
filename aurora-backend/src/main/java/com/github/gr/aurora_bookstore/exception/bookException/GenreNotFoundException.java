package com.github.gr.aurora_bookstore.exception.bookException;

public class GenreNotFoundException extends ResourceNotFoundException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}
