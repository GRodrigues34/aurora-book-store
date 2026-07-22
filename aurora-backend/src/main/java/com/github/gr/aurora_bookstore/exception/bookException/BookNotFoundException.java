package com.github.gr.aurora_bookstore.exception.bookException;

public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
