// src/main/java/com/riceshop/orderservice/dto/response/CartResponse.java
package com.riceshop.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
}