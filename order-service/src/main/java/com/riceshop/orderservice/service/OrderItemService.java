// src/main/java/com/riceshop/orderservice/service/OrderItemService.java
package com.riceshop.orderservice.service;

import com.riceshop.orderservice.dto.response.OrderItemResponse;
import com.riceshop.orderservice.entity.Order;
import com.riceshop.orderservice.entity.OrderItem;
import com.riceshop.orderservice.exception.ResourceNotFoundException;
import com.riceshop.orderservice.repository.OrderItemRepository;
import com.riceshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItemsByOrderId(Long orderId) {
        // Check if order exists
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderItemResponse getOrderItemById(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + id));

        return toOrderItemResponse(orderItem);
    }

    @Transactional
    public List<Object[]> getTopSellingProducts(int limit) {
        return orderItemRepository.findTopSellingProducts().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .productImage(orderItem.getProductImage())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .subtotal(orderItem.getTotalPrice())
                .build();
    }
}