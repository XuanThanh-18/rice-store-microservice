// src/main/java/com/riceshop/orderservice/exception/ShippingProcessingException.java
package com.riceshop.orderservice.exception;

public class ShippingProcessingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ShippingProcessingException(String message) {
        super(message);
    }
}