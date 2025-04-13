// src/main/java/com/riceshop/orderservice/service/CartService.java
package com.riceshop.orderservice.service;

import com.riceshop.orderservice.client.ProductServiceClient;
import com.riceshop.orderservice.dto.external.ProductDto;
import com.riceshop.orderservice.dto.request.CartItemRequest;
import com.riceshop.orderservice.dto.response.CartItemResponse;
import com.riceshop.orderservice.dto.response.CartResponse;
import com.riceshop.orderservice.entity.Cart;
import com.riceshop.orderservice.entity.CartItem;
import com.riceshop.orderservice.exception.ResourceNotFoundException;
import com.riceshop.orderservice.repository.CartItemRepository;
import com.riceshop.orderservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(Long userId, CartItemRequest request) {
        // Validate product exists and has stock
        ProductDto product = productServiceClient.getProduct(request.getProductId()).getBody();
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available. Available: " + product.getStockQuantity());
        }

        // Get or create cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        // Check if item already exists in cart
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            // Update quantity if item exists
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            // Create new cart item if it doesn't exist
            cartItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .build();
            cart.addCartItem(cartItem);
        }

        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long userId, Long productId, CartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        // Validate product and stock
        ProductDto product = productServiceClient.getProduct(productId).getBody();
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available. Available: " + product.getStockQuantity());
        }

        // Update quantity or remove if quantity is 0
        if (request.getQuantity() <= 0) {
            cart.removeCartItem(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);

        return buildCartResponse(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        cart.clearCart();
        cartRepository.save(cart);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .build();
        return cartRepository.save(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            ProductDto product = productServiceClient.getProduct(item.getProductId()).getBody();

            if (product != null) {
                BigDecimal price = product.getPrice();
                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

                CartItemResponse itemResponse = CartItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(product.getName())
                        .productImage(product.getImage())
                        .price(price)
                        .quantity(item.getQuantity())
                        .subtotal(subtotal)
                        .build();

                itemResponses.add(itemResponse);
                totalAmount = totalAmount.add(subtotal);
            }
        }

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .totalItems(itemResponses.size())
                .build();
    }
}