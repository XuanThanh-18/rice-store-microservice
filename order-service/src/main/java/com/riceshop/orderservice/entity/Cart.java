// src/main/java/com/riceshop/orderservice/entity/Cart.java
package com.riceshop.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper method to add items to the cart
    public void addCartItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    // Helper method to remove items from the cart
    public void removeCartItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }

    // Helper method to clear the cart
    public void clearCart() {
        items.clear();
    }
}