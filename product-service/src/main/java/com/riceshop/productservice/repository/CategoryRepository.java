// src/main/java/com/riceshop/productservice/repository/CategoryRepository.java
package com.riceshop.productservice.repository;

import com.riceshop.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();

    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}