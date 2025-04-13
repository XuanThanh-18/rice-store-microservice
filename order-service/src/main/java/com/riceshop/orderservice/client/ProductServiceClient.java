// src/main/java/com/riceshop/orderservice/client/ProductServiceClient.java
package com.riceshop.orderservice.client;

import com.riceshop.orderservice.dto.external.ProductDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    ResponseEntity<ProductDto> getProduct(@PathVariable Long id);

    @PutMapping("/api/products/{id}/stock")
    @CircuitBreaker(name = "productService", fallbackMethod = "updateStockFallback")
    ResponseEntity<Void> updateStock(@PathVariable Long id, @RequestParam Integer quantity);

    default ResponseEntity<ProductDto> getProductFallback(Long id, Throwable throwable) {
        return ResponseEntity.ok(ProductDto.builder()
                .id(id)
                .name("Fallback Product")
                .price(null)
                .stockQuantity(0)
                .build());
    }

    default ResponseEntity<Void> updateStockFallback(Long id, Integer quantity, Throwable throwable) {
        // Log the fallback and implement retry logic later
        return ResponseEntity.ok().build();
    }
}