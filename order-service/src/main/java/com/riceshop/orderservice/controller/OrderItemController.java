// src/main/java/com/riceshop/orderservice/controller/OrderItemController.java
package com.riceshop.orderservice.controller;

import com.riceshop.orderservice.dto.response.OrderItemResponse;
import com.riceshop.orderservice.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<OrderItemResponse>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @GetMapping("/top-selling")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(orderItemService.getTopSellingProducts(limit));
    }
}