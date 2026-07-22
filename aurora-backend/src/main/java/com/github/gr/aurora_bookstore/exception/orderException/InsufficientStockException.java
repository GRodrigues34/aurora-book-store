package com.github.gr.aurora_bookstore.exception.orderException;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
