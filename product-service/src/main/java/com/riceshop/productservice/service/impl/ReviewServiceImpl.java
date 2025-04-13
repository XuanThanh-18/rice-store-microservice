// src/main/java/com/riceshop/productservice/service/ReviewService.java
package com.riceshop.productservice.service.impl;

import com.riceshop.productservice.dto.request.ReviewRequest;
import com.riceshop.productservice.dto.response.ReviewResponse;
import com.riceshop.productservice.entity.Product;
import com.riceshop.productservice.entity.Review;
import com.riceshop.productservice.exception.ResourceNotFoundException;
import com.riceshop.productservice.repository.ProductRepository;
import com.riceshop.productservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements com.riceshop.productservice.service.ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        // Check if product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        return reviews.map(review -> modelMapper.map(review, ReviewResponse.class));
    }

    @Transactional
    @Override
    public ReviewResponse createReview(Long productId, Long userId, String username, ReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if user already reviewed this product
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUserId(userId);
        review.setUsername(username);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        return modelMapper.map(savedReview, ReviewResponse.class);
    }

    @Transactional
    @Override
    public ReviewResponse updateReview(Long id, Long userId, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        // Check if review belongs to user
        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        return modelMapper.map(updatedReview, ReviewResponse.class);
    }

    @Transactional
    @Override
    public void deleteReview(Long id, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        // Check if review belongs to user or user is admin
        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }
}