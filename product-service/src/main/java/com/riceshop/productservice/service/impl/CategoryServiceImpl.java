// src/main/java/com/riceshop/productservice/service/CategoryService.java
package com.riceshop.productservice.service.impl;

import com.riceshop.productservice.dto.request.CategoryRequest;
import com.riceshop.productservice.dto.response.CategoryResponse;
import com.riceshop.productservice.entity.Category;
import com.riceshop.productservice.exception.ResourceNotFoundException;
import com.riceshop.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements com.riceshop.productservice.service.CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return mapToCategoryResponse(category);
    }

    @Transactional
    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        if (request.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + request.getParentId()));
            category.setParent(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }

    @Transactional
    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            // Check if parent id is different from current parent
            if (category.getParent() == null || !category.getParent().getId().equals(request.getParentId())) {
                // Check for circular reference
                if (request.getParentId().equals(id)) {
                    throw new IllegalArgumentException("Cannot set category as its own parent");
                }

                Category parentCategory = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + request.getParentId()));
                category.setParent(parentCategory);
            }
        } else {
            category.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(updatedCategory);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getChildren().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with children");
        }

        if (!category.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with products");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        CategoryResponse response = modelMapper.map(category, CategoryResponse.class);

        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
            response.setParentName(category.getParent().getName());
        }

        if (!category.getChildren().isEmpty()) {
            List<CategoryResponse> childResponses = category.getChildren().stream()
                    .map(this::mapToCategoryResponse)
                    .collect(Collectors.toList());
            response.setChildren(childResponses);
        }

        return response;
    }
}