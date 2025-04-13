// src/main/java/com/riceshop/productservice/service/ProductService.java
package com.riceshop.productservice.service.impl;

import com.riceshop.productservice.dto.request.ProductRequest;
import com.riceshop.productservice.dto.response.ProductResponse;
import com.riceshop.productservice.dto.response.ReviewResponse;
import com.riceshop.productservice.entity.Category;
import com.riceshop.productservice.entity.Product;
import com.riceshop.productservice.exception.ResourceNotFoundException;
import com.riceshop.productservice.repository.CategoryRepository;
import com.riceshop.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements com.riceshop.productservice.service.ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    @Value("${file.upload-dir}")
    private String uploadDir ;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        return products.map(this::mapToProductResponse);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.search(keyword, pageable);
        return products.map(this::mapToProductResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return mapToProductResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setRiceType(request.getRiceType());
        product.setOrigin(request.getOrigin());
        product.setPackaging(request.getPackaging());
        product.setWeight(request.getWeight());
        product.setIsOrganic(request.getIsOrganic());
        product.setCookingInstructions(request.getCookingInstructions());
        product.setNutritionFacts(request.getNutritionFacts());

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setRiceType(request.getRiceType());
        product.setOrigin(request.getOrigin());
        product.setPackaging(request.getPackaging());
        product.setWeight(request.getWeight());
        product.setIsOrganic(request.getIsOrganic());
        product.setCookingInstructions(request.getCookingInstructions());
        product.setNutritionFacts(request.getNutritionFacts());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Transactional
    @Override
    public String uploadProductImage(Long id, MultipartFile file) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + fileExtension;

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // Update product image path
        product.setImage("/uploads/products/" + fileName);
        productRepository.save(product);

        return product.getImage();
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Delete product image if exists
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                Path imagePath = Paths.get("." + product.getImage());
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                // Log error but continue with product deletion
                e.printStackTrace();
            }
        }

        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> getNewProducts() {
        List<Product> products = productRepository.findTop5ByOrderByCreatedAtDesc();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getTopRatedProducts() {
        List<Product> products = productRepository.findTop5ByAverageRating();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setAverageRating(product.getAverageRating());
        response.setReviewCount(product.getReviews().size());

        return response;
    }

    private ProductResponse mapToDetailedProductResponse(Product product) {
        ProductResponse response = mapToProductResponse(product);

        List<ReviewResponse> reviewResponses = product.getReviews().stream()
                .map(review -> modelMapper.map(review, ReviewResponse.class))
                .collect(Collectors.toList());

        response.setReviews(reviewResponses);

        return response;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return "";
        }

        return fileName.substring(dotIndex);
    }
}