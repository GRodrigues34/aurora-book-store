package com.github.gr.aurora_bookstore.exception.orderException;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
