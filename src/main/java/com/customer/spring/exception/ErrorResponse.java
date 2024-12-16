package com.customer.spring.exception;


import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String timestamp;
}
