// src/main/java/com/riceshop/orderservice/repository/OrderRepository.java
package com.riceshop.orderservice.repository;

import com.riceshop.orderservice.entity.Order;
import com.riceshop.orderservice.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    long countByStatus(OrderStatus status);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PAID' AND o.status = 'DELIVERED'")
    Page<Order> findCompletedOrders(Pageable pageable);

    @Query("SELECT FUNCTION('DATE', o.createdAt) as orderDate, COUNT(o) as orderCount, SUM(o.totalAmount) as totalAmount " +
            "FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', o.createdAt)")
    List<Object[]> getOrderStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o.paymentMethod, COUNT(o) as count FROM Order o GROUP BY o.paymentMethod")
    List<Object[]> countOrdersByPaymentMethod();
}