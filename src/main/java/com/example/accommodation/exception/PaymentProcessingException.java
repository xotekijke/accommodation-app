package com.example.accommodation.exception;

public class PaymentProcessingException extends RuntimeException {

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
