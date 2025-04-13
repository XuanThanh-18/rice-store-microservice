// src/main/java/com/riceshop/orderservice/dto/external/PaymentRequestDto.java
package com.riceshop.orderservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
}