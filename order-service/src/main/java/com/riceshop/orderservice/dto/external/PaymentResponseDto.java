// src/main/java/com/riceshop/orderservice/dto/external/PaymentResponseDto.java
package com.riceshop.orderservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private String transactionId;
    private String paymentMethod;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}