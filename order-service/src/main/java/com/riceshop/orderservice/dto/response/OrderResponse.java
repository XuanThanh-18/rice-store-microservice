// src/main/java/com/riceshop/orderservice/dto/response/OrderResponse.java
package com.riceshop.orderservice.dto.response;

import com.riceshop.orderservice.entity.Order;
import com.riceshop.orderservice.entity.enums.OrderStatus;
import com.riceshop.orderservice.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private String customerNotes;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}