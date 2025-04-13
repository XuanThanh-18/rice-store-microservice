package com.riceshop.productservice.exception.error;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorDetails extends ErrorDetails {
    private Map<String, String> errors;

    public ValidationErrorDetails(LocalDateTime timestamp, String message, String path, String errorCode,
                                  Map<String, String> errors) {
        super(timestamp, message, path, errorCode);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
