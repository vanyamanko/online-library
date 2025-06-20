package com.example.review_service.repository;

import com.example.review_service.model.ReviewReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {
    Optional<ReviewReaction> findByReviewIdAndUserId(String reviewId, String userId);
    void deleteByReviewIdAndUserId(String id, String userId);
}