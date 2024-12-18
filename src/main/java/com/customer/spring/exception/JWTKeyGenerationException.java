package com.customer.spring.exception;

public class JWTKeyGenerationException extends Exception {
    public JWTKeyGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

