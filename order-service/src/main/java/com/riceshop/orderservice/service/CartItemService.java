// src/main/java/com/riceshop/orderservice/service/CartItemService.java
package com.riceshop.orderservice.service;

import com.riceshop.orderservice.client.ProductServiceClient;
import com.riceshop.orderservice.dto.external.ProductDto;
import com.riceshop.orderservice.dto.response.CartItemResponse;
import com.riceshop.orderservice.entity.CartItem;
import com.riceshop.orderservice.exception.ResourceNotFoundException;
import com.riceshop.orderservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItemsByCartId(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);

        return cartItems.stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CartItemResponse getCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + id));

        return toCartItemResponse(cartItem);
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        ProductDto product = productServiceClient.getProduct(cartItem.getProductId()).getBody();

        if (product == null) {
            return CartItemResponse.builder()
                    .id(cartItem.getId())
                    .productId(cartItem.getProductId())
                    .productName("Unknown Product")
                    .price(BigDecimal.ZERO)
                    .quantity(cartItem.getQuantity())
                    .subtotal(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProductId())
                .productName(product.getName())
                .productImage(product.getImage())
                .price(product.getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}