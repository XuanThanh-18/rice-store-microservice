// src/test/java/com/riceshop/orderservice/service/CartServiceTest.java
package com.riceshop.orderservice.service;

import com.riceshop.orderservice.client.ProductServiceClient;
import com.riceshop.orderservice.dto.external.ProductDto;
import com.riceshop.orderservice.dto.request.CartItemRequest;
import com.riceshop.orderservice.dto.response.CartResponse;
import com.riceshop.orderservice.entity.Cart;
import com.riceshop.orderservice.repository.CartItemRepository;
import com.riceshop.orderservice.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private ProductDto testProduct;
    private CartItemRequest testRequest;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        testCart = Cart.builder()
                .id(1L)
                .userId(userId)
                .items(new ArrayList<>())
                .build();

        testProduct = ProductDto.builder()
                .id(1L)
                .name("Test Rice")
                .price(new BigDecimal("15.99"))
                .stockQuantity(10)
                .build();

        testRequest = CartItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();
    }

    @Test
    void getCart_ExistingCart_ReturnsCart() {
        // Arrange
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(testCart));

        // Act
        CartResponse response = cartService.getCart(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        verify(cartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getCart_NonExistingCart_CreatesAndReturnsNewCart() {
        // Arrange
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        CartResponse response = cartService.getCart(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    // Thêm nhiều test cases khác...
}