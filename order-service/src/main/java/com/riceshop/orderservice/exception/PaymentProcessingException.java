// src/main/java/com/riceshop/orderservice/exception/PaymentProcessingException.java
package com.riceshop.orderservice.exception;

public class PaymentProcessingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PaymentProcessingException(String message) {
        super(message);
    }
}