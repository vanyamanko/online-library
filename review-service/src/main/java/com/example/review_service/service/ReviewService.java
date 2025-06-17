package com.example.review_service.service;

import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.model.Review;

public interface ReviewService {
    public Review createReview(String token, CreateReviewRequest createReviewRequest);
}
