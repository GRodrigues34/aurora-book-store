package com.github.gr.aurora_bookstore.exception.orderException;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException(String message) {
        super(message);
    }
}
