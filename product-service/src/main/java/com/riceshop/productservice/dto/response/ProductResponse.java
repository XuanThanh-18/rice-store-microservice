// src/main/java/com/riceshop/productservice/dto/response/ProductResponse.java
package com.riceshop.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    private String image;
    private String riceType;
    private String origin;
    private String packaging;
    private Double weight;
    private Boolean isOrganic;
    private String cookingInstructions;
    private String nutritionFacts;
    private Double averageRating;
    private Integer reviewCount;
    private List<ReviewResponse> reviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}