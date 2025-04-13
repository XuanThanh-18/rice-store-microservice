package com.riceshop.orderservice.entity;

import com.riceshop.orderservice.entity.enums.OrderStatus;
import com.riceshop.orderservice.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    BigDecimal TotalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status;

    @Column(nullable = false,columnDefinition = "TEXT")
    String shippingAddress;

    @Column(nullable = false)
    String paymentMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    String customerNotes;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // Helper method to add items to the order
    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    // Helper method to remove items from the order
    public void removeOrderItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    // Helper method to calculate total amount
    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
