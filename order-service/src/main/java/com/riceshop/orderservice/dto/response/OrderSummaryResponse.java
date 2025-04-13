// src/main/java/com/riceshop/orderservice/dto/response/OrderSummaryResponse.java
package com.riceshop.orderservice.dto.response;

import com.riceshop.orderservice.entity.Order;
import com.riceshop.orderservice.entity.enums.OrderStatus;
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
public class OrderSummaryResponse {
    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private Integer totalItems;
}