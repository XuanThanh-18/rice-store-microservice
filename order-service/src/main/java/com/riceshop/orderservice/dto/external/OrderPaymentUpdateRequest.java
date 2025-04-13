// src/main/java/com/riceshop/orderservice/dto/external/OrderPaymentUpdateRequest.java
package com.riceshop.orderservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentUpdateRequest {
    private String paymentStatus;
}