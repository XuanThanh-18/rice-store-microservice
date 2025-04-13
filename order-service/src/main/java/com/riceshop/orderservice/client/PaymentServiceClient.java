// src/main/java/com/riceshop/orderservice/client/PaymentServiceClient.java
package com.riceshop.orderservice.client;

import com.riceshop.orderservice.dto.external.PaymentRequestDto;
import com.riceshop.orderservice.dto.external.PaymentResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payments")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "createPaymentFallback")
    ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto request);

    @GetMapping("/api/payments/order/{orderId}")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "getPaymentByOrderIdFallback")
    ResponseEntity<PaymentResponseDto> getPaymentByOrderId(@PathVariable Long orderId);

    @PutMapping("/api/payments/order/{orderId}/cancel")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "cancelPaymentFallback")
    ResponseEntity<Void> cancelPayment(@PathVariable Long orderId);

    default ResponseEntity<PaymentResponseDto> createPaymentFallback(PaymentRequestDto request, Throwable throwable) {
        // Fallback logic
        return ResponseEntity.ok(PaymentResponseDto.builder()
                .orderId(request.getOrderId())
                .status("UNKNOWN")
                .message("Payment service is currently unavailable")
                .build());
    }

    default ResponseEntity<PaymentResponseDto> getPaymentByOrderIdFallback(Long orderId, Throwable throwable) {
        // Fallback logic
        return ResponseEntity.ok(PaymentResponseDto.builder()
                .orderId(orderId)
                .status("UNKNOWN")
                .message("Payment service is currently unavailable")
                .build());
    }

    default ResponseEntity<Void> cancelPaymentFallback(Long orderId, Throwable throwable) {
        // Log and return empty response
        return ResponseEntity.ok().build();
    }
}