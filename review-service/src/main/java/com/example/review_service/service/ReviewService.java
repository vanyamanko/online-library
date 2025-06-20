package com.example.review_service.service;

import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.model.Review;
import com.example.review_service.model.ReviewReaction;

public interface ReviewService {
    public Review createReview(String token, CreateReviewRequest createReviewRequest);
    public void deleteByBookId(String bookId);
    public void deleteReviewById(String token, String id);
    public void reviewReaction(String token, String id, ReviewReaction.ReactionType reactionType);
}
