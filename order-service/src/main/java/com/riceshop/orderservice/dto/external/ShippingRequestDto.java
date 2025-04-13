// src/main/java/com/riceshop/orderservice/dto/external/ShippingRequestDto.java
package com.riceshop.orderservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingRequestDto {
    private Long orderId;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private Double weight;
    private String shippingMethod;
}