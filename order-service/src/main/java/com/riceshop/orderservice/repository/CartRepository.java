// src/main/java/com/riceshop/orderservice/repository/CartRepository.java
package com.riceshop.orderservice.repository;

import com.riceshop.orderservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserIdWithItems(Long userId);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.items.size > 0")
    long countNonEmptyCarts();

    @Query("SELECT c FROM Cart c WHERE c.items.size > 0 ORDER BY c.updatedAt DESC")
    List<Cart> findNonEmptyCartsOrderByUpdatedAtDesc();

    @Query("SELECT c FROM Cart c WHERE c.updatedAt < :date AND c.items.size > 0")
    List<Cart> findAbandonedCarts(java.time.LocalDateTime date);

    void deleteByUserId(Long userId);
}