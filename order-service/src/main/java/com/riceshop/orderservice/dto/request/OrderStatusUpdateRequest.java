// src/main/java/com/riceshop/orderservice/dto/request/OrderStatusUpdateRequest.java
package com.riceshop.orderservice.dto.request;

import com.riceshop.orderservice.entity.Order;
import com.riceshop.orderservice.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}