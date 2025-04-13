package com.riceshop.productservice.service;

import com.riceshop.productservice.dto.request.ProductRequest;
import com.riceshop.productservice.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Page<ProductResponse> getAllProducts(Pageable pageable);

    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);

    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    ProductResponse getProductById(Long id);

    @Transactional
    ProductResponse createProduct(ProductRequest request);

    @Transactional
    ProductResponse updateProduct(Long id, ProductRequest request);

    @Transactional
    String uploadProductImage(Long id, MultipartFile file) throws IOException;

    @Transactional
    void deleteProduct(Long id);

    List<ProductResponse> getNewProducts();

    List<ProductResponse> getTopRatedProducts();
}
