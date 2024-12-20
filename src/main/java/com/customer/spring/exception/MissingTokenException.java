package com.customer.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class MissingTokenException extends RuntimeException {
    public MissingTokenException(String message) {
        super(message);
    }
}

