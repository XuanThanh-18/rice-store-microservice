// src/main/java/com/riceshop/productservice/controller/ReviewController.java
package com.riceshop.productservice.controller;

import com.riceshop.productservice.dto.request.ReviewRequest;
import com.riceshop.productservice.dto.response.ReviewResponse;
import com.riceshop.productservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request,
            @RequestParam Long userId,
            @RequestParam String username) {
        return new ResponseEntity<>(
                reviewService.createReview(productId, userId, username, request),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long productId,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @RequestParam Long userId) {
        return ResponseEntity.ok(reviewService.updateReview(id, userId, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long id,
            @RequestParam Long userId) {
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }
}