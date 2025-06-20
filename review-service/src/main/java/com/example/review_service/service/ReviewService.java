package com.example.review_service.service;

import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.model.Review;
import com.example.review_service.model.ReviewReaction;

public interface ReviewService {
    Review createReview(String token, CreateReviewRequest createReviewRequest);

    void deleteByBookId(String bookId);

    void deleteReviewById(String token, String id);

    void reviewReaction(String token, String id, ReviewReaction.ReactionType reactionType);
}
