package com.riceshop.productservice.service;

import com.riceshop.productservice.dto.request.ReviewRequest;
import com.riceshop.productservice.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewService {
    Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable);

    @Transactional
    ReviewResponse createReview(Long productId, Long userId, String username, ReviewRequest request);

    @Transactional
    ReviewResponse updateReview(Long id, Long userId, ReviewRequest request);

    @Transactional
    void deleteReview(Long id, Long userId);
}
