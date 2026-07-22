package com.github.gr.aurora_bookstore.exception.bookException;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
