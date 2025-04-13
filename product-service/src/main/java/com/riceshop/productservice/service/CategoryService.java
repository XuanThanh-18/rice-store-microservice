package com.riceshop.productservice.service;

import com.riceshop.productservice.dto.request.CategoryRequest;
import com.riceshop.productservice.dto.response.CategoryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    @Transactional
    CategoryResponse createCategory(CategoryRequest request);

    @Transactional
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    @Transactional
    void deleteCategory(Long id);
}
