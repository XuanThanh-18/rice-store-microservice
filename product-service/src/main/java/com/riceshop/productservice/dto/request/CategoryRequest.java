// src/main/java/com/riceshop/productservice/dto/request/CategoryRequest.java
package com.riceshop.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private Long parentId;
}