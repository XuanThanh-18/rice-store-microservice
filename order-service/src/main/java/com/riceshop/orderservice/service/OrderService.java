// src/main/java/com/riceshop/orderservice/service/OrderService.java
package com.riceshop.orderservice.service;

import com.riceshop.orderservice.client.ProductServiceClient;
import com.riceshop.orderservice.client.UserServiceClient;
import com.riceshop.orderservice.dto.external.*;
import com.riceshop.orderservice.dto.request.OrderRequest;
import com.riceshop.orderservice.dto.request.OrderStatusUpdateRequest;
import com.riceshop.orderservice.dto.response.OrderItemResponse;
import com.riceshop.orderservice.dto.response.OrderResponse;
import com.riceshop.orderservice.dto.response.OrderSummaryResponse;
import com.riceshop.orderservice.entity.Cart;
import com.riceshop.orderservice.entity.CartItem;
import com.riceshop.orderservice.entity.Order;
import com.riceshop.orderservice.entity.OrderItem;
import com.riceshop.orderservice.entity.enums.OrderStatus;
import com.riceshop.orderservice.entity.enums.PaymentStatus;
import com.riceshop.orderservice.exception.ResourceNotFoundException;
import com.riceshop.orderservice.repository.CartRepository;
import com.riceshop.orderservice.repository.OrderItemRepository;
import com.riceshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.riceshop.orderservice.entity.enums.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService; //
    // Conditional dependencies
    @Autowired(required = false)
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired(required = false)
    private ShippingIntegrationService shippingIntegrationService;

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::toOrderSummaryResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::toOrderSummaryResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatus(status, pageable);
        return orders.map(this::toOrderSummaryResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        // Get user cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order with empty cart");
        }

        // Validate user exists
        UserDto user = userServiceClient.getUser(userId).getBody();
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Create new order
        Order order = Order.builder()
                .userId(userId)
                .TotalAmount(BigDecimal.ZERO) // Will be calculated
                .status(PENDING)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .customerNotes(request.getCustomerNotes())
                .items(new ArrayList<>())
                .build();

        // Add items from cart to order
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            ProductDto product = productServiceClient.getProduct(cartItem.getProductId()).getBody();

            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + cartItem.getProductId());
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .productImage(product.getImage())
                    .build();

            order.addOrderItem(orderItem);

            // Update total amount
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // Update product stock
            productServiceClient.updateStock(product.getId(), -cartItem.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Clear the cart after successful order creation
        cartService.clearCart(userId);

        return toOrderResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Validate status transition
        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());

        // If order is cancelled, restore product stock
        if (request.getStatus() == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                productServiceClient.updateStock(item.getProductId(), item.getQuantity());
            }
            order.setPaymentStatus(PaymentStatus.FAILED);
        }

        Order updatedOrder = orderRepository.save(order);
        return toOrderResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setPaymentStatus(paymentStatus);

        // If payment is successful, update order status to PROCESSING if it's PENDING
        if (paymentStatus == PaymentStatus.PAID && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PROCESSING);
        }

        Order updatedOrder = orderRepository.save(order);
        return toOrderResponse(updatedOrder);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Implement status transition validation logic
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.PROCESSING && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("Invalid status transition from PENDING to " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("Invalid status transition from PROCESSING to " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new IllegalArgumentException("Invalid status transition from SHIPPED to " + newStatus);
                }
                break;
            case DELIVERED:
                throw new IllegalArgumentException("Cannot change status of a DELIVERED order");
            case CANCELLED:
                throw new IllegalArgumentException("Cannot change status of a CANCELLED order");
            default:
                throw new IllegalArgumentException("Unknown order status: " + currentStatus);
        }
    }

    private OrderResponse toOrderResponse(Order order) {
        // Get user information
        UserDto user = userServiceClient.getUser(order.getUserId()).getBody();
        String username = user != null ? user.getUsername() : "Unknown";

        // Map order items
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .username(username)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .customerNotes(order.getCustomerNotes())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderSummaryResponse toOrderSummaryResponse(Order order) {
        return OrderSummaryResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .totalItems(order.getItems().size())
                .build();
    }

    @Transactional
    public OrderResponse processOrderPayment(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Kiểm tra xem dịch vụ thanh toán có sẵn không
        if (paymentIntegrationService != null) {
            // Khởi tạo thanh toán
            PaymentResponseDto paymentResponse = paymentIntegrationService.initiatePayment(order);
            // Xử lý kết quả thanh toán
        } else {
            log.warn("Payment integration is not enabled. Skip payment processing for order: {}", id);
            // Thực hiện xử lý thay thế
        }

        // Gửi thông báo
        notificationService.sendOrderConfirmation(order);

        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponse processOrderShipping(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order must be in PROCESSING state to arrange shipping");
        }

        // Sắp xếp vận chuyển
        ShippingResponseDto shippingResponse = shippingIntegrationService.createShippingOrder(order);

        // Cập nhật mã theo dõi trong order nếu cần
        // order.setTrackingNumber(shippingResponse.getTrackingNumber());

        // Cập nhật trạng thái
        order.setStatus(SHIPPED);
        Order updatedOrder = orderRepository.save(order);

        // Gửi thông báo
        notificationService.sendShippingUpdate(updatedOrder, shippingResponse.getTrackingNumber());

        return toOrderResponse(updatedOrder);
    }
}