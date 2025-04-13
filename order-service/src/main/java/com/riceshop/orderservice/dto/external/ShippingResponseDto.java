// src/main/java/com/riceshop/orderservice/dto/external/ShippingResponseDto.java
package com.riceshop.orderservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponseDto {
    private Long id;
    private Long orderId;
    private String trackingNumber;
    private String status;
    private String carrier;
    private String estimatedDelivery;
    private String currentLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}