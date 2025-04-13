// src/main/java/com/riceshop/orderservice/repository/OrderItemRepository.java
package com.riceshop.orderservice.repository;

import com.riceshop.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi.productId, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts();

    @Query("SELECT oi.productId, SUM(oi.quantity * oi.price) FROM OrderItem oi GROUP BY oi.productId ORDER BY SUM(oi.quantity * oi.price) DESC")
    List<Object[]> findTopRevenueProducts();
}