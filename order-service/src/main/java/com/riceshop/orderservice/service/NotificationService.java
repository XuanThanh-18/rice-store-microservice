// src/main/java/com/riceshop/orderservice/service/NotificationService.java
package com.riceshop.orderservice.service;

import com.riceshop.orderservice.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    // Có thể thêm EmailService hoặc SMSService khi triển khai

    public void sendOrderConfirmation(Order order) {
        // Mô phỏng việc gửi email xác nhận đơn hàng
        log.info("Sending order confirmation email for order ID: {} to user ID: {}",
                order.getId(), order.getUserId());
    }

    public void sendOrderStatusUpdate(Order order) {
        // Mô phỏng việc gửi email cập nhật trạng thái đơn hàng
        log.info("Sending order status update email for order ID: {}, new status: {} to user ID: {}",
                order.getId(), order.getStatus(), order.getUserId());
    }

    public void sendPaymentConfirmation(Order order) {
        // Mô phỏng việc gửi email xác nhận thanh toán
        log.info("Sending payment confirmation email for order ID: {} to user ID: {}",
                order.getId(), order.getUserId());
    }

    public void sendShippingUpdate(Order order, String trackingNumber) {
        // Mô phỏng việc gửi email cập nhật vận chuyển
        log.info("Sending shipping update email for order ID: {}, tracking number: {} to user ID: {}",
                order.getId(), trackingNumber, order.getUserId());
    }
}