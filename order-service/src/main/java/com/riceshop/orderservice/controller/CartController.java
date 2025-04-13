// src/main/java/com/riceshop/orderservice/controller/CartController.java
package com.riceshop.orderservice.controller;

import com.riceshop.orderservice.dto.request.CartItemRequest;
import com.riceshop.orderservice.dto.response.CartResponse;
import com.riceshop.orderservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    @PutMapping("/{userId}/items/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, productId, request));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}