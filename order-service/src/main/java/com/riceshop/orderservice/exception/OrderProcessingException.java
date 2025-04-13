// src/main/java/com/riceshop/orderservice/exception/OrderProcessingException.java
package com.riceshop.orderservice.exception;

public class OrderProcessingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderProcessingException(String message) {
        super(message);
    }
}