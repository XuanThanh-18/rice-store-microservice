// src/main/java/com/riceshop/orderservice/repository/CartItemRepository.java
package com.riceshop.orderservice.repository;

import com.riceshop.orderservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.productId = :productId")
    void deleteByCartIdAndProductId(Long cartId, Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(Long cartId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.id = :cartId")
    int countByCartId(Long cartId);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Integer getTotalItemsInCart(Long cartId);
}