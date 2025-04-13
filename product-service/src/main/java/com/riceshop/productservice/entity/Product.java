// src/main/java/com/riceshop/productservice/entity/Product.java
package com.riceshop.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String image;

    @Column(name = "rice_type")
    private String riceType;

    @Column(name = "origin")
    private String origin;

    @Column(name = "packaging")
    private String packaging;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "is_organic")
    private Boolean isOrganic;

    @Column(name = "cooking_instructions", columnDefinition = "TEXT")
    private String cookingInstructions;

    @Column(name = "nutrition_facts", columnDefinition = "TEXT")
    private String nutritionFacts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Calculate average rating
    @Transient
    public Double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        Double sum = 0.0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }
}