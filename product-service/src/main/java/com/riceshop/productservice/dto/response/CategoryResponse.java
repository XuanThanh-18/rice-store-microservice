// src/main/java/com/riceshop/productservice/dto/response/CategoryResponse.java
package com.riceshop.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private List<CategoryResponse> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}