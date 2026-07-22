package com.github.gr.aurora_bookstore.exception.cartException;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
