// src/main/java/com/riceshop/orderservice/service/PaymentIntegrationService.java
package com.riceshop.orderservice.service;

import com.riceshop.orderservice.client.PaymentServiceClient;
import com.riceshop.orderservice.dto.external.PaymentRequestDto;
import com.riceshop.orderservice.dto.external.PaymentResponseDto;
import com.riceshop.orderservice.entity.Order;
import com.riceshop.orderservice.exception.PaymentProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentIntegrationService {

    private final PaymentServiceClient paymentServiceClient;

    public PaymentResponseDto initiatePayment(Order order) {
        try {
            PaymentRequestDto request = PaymentRequestDto.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .amount(order.getTotalAmount())
                    .paymentMethod(order.getPaymentMethod())
                    .build();

            return paymentServiceClient.createPayment(request).getBody();
        } catch (Exception e) {
            log.error("Error initiating payment for order {}: {}", order.getId(), e.getMessage());
            throw new PaymentProcessingException("Failed to initiate payment: " + e.getMessage());
        }
    }

    public PaymentResponseDto getPaymentStatus(Long orderId) {
        try {
            return paymentServiceClient.getPaymentByOrderId(orderId).getBody();
        } catch (Exception e) {
            log.error("Error getting payment status for order {}: {}", orderId, e.getMessage());
            throw new PaymentProcessingException("Failed to get payment status: " + e.getMessage());
        }
    }

    public void cancelPayment(Long orderId) {
        try {
            paymentServiceClient.cancelPayment(orderId);
        } catch (Exception e) {
            log.error("Error cancelling payment for order {}: {}", orderId, e.getMessage());
            throw new PaymentProcessingException("Failed to cancel payment: " + e.getMessage());
        }
    }
}