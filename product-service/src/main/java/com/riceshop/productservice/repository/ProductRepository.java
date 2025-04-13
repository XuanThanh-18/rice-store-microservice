// src/main/java/com/riceshop/productservice/repository/ProductRepository.java
package com.riceshop.productservice.repository;

import com.riceshop.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.riceType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.origin) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> search(String keyword, Pageable pageable);

    List<Product> findTop5ByOrderByCreatedAtDesc();

    @Query(value = "SELECT p.* FROM products p " +
            "JOIN (SELECT product_id, AVG(rating) as avg_rating FROM reviews GROUP BY product_id) r " +
            "ON p.id = r.product_id " +
            "ORDER BY r.avg_rating DESC LIMIT 5", nativeQuery = true)
    List<Product> findTop5ByAverageRating();
}