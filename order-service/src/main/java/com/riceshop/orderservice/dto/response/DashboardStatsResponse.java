// src/main/java/com/riceshop/orderservice/dto/response/DashboardStatsResponse.java
package com.riceshop.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;
    private long cancelledOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private Map<String, Long> ordersByStatus;
    private Map<String, Long> ordersByPaymentMethod;
    private LocalDateTime lastUpdated;
}