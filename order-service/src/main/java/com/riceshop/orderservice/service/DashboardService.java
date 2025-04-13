// src/main/java/com/riceshop/orderservice/service/DashboardService.java
package com.riceshop.orderservice.service;

import com.riceshop.orderservice.dto.response.DashboardStatsResponse;
import com.riceshop.orderservice.entity.Order;
import com.riceshop.orderservice.entity.enums.OrderStatus;
import com.riceshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        long shippedOrders = orderRepository.countByStatus(OrderStatus.SHIPPED);
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        // Calculate total revenue from completed orders
        BigDecimal totalRevenue = BigDecimal.ZERO;
        List<Order> completedOrders = orderRepository.findCompletedOrders(null).getContent();
        for (Order order : completedOrders) {
            totalRevenue = totalRevenue.add(order.getTotalAmount());
        }

        // Calculate average order value
        BigDecimal averageOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(completedOrders.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Prepare order by status map
        Map<String, Long> ordersByStatus = new HashMap<>();
        ordersByStatus.put("PENDING", pendingOrders);
        ordersByStatus.put("PROCESSING", processingOrders);
        ordersByStatus.put("SHIPPED", shippedOrders);
        ordersByStatus.put("DELIVERED", deliveredOrders);
        ordersByStatus.put("CANCELLED", cancelledOrders);

        // Get orders by payment method
        Map<String, Long> ordersByPaymentMethod = new HashMap<>();
        List<Object[]> paymentMethodStats = orderRepository.countOrdersByPaymentMethod();
        for (Object[] stat : paymentMethodStats) {
            String paymentMethod = (String) stat[0];
            Long count = (Long) stat[1];
            ordersByPaymentMethod.put(paymentMethod, count);
        }

        return DashboardStatsResponse.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .ordersByStatus(ordersByStatus)
                .ordersByPaymentMethod(ordersByPaymentMethod)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}