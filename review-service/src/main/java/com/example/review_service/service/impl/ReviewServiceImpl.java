package com.example.review_service.service.impl;

import com.example.review_service.component.AuthComponent;
import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.dto.UpdateBookRatingRequest;
import com.example.review_service.dto.ValidationResponse;
import com.example.review_service.kafka.KafkaProduser;
import com.example.review_service.model.Review;
import com.example.review_service.model.ReviewReaction;
import com.example.review_service.repository.ReviewReactionRepository;
import com.example.review_service.repository.ReviewRepository;
import com.example.review_service.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReactionRepository reviewReactionRepository;
    private final AuthComponent authComponent;
    private final KafkaProduser kafkaProduser;

    public Review createReview(String token, CreateReviewRequest createReviewRequest) {
        ValidationResponse response = authComponent.validateToken(token);
        if (!response.isSuccessfully()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        if (reviewRepository.existsByUserIdAndBookId(response.getUserId(), createReviewRequest.getBookId())) {
            throw new DataIntegrityViolationException("There is already a review");
        }

        Review review = Review.builder()
                .id("review_" + UUID.randomUUID())
                .rating(createReviewRequest.getRating())
                .text(createReviewRequest.getText())
                .bookId(createReviewRequest.getBookId())
                .comments(new ArrayList<>())
                .likes(0)
                .dislikes(0)
                .createdAt(Instant.now().toString())
                .userId(response.getUserId())
                .build();
        UpdateBookRatingRequest updateBookRatingRequest = UpdateBookRatingRequest.builder()
                .bookId(review.getBookId())
                .rating(review.getRating())
                .build();

        kafkaProduser.sendToBookServiceAboutNewRating(updateBookRatingRequest);

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteByBookId(String bookId) {
        reviewRepository.deleteByBookId(bookId);
    }

    public void deleteReviewById(String token, String id) {
        ValidationResponse response = authComponent.validateToken(token);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        if (response.isSuccessfully() &&
                (response.getRole().equals("ADMIN") || response.getUserId().equals(review.getUserId()))
        ) {
            reviewRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    @Transactional
    public void reviewReaction(String token, String id, ReviewReaction.ReactionType reactionType) {
        ValidationResponse response = authComponent.validateToken(token);
        if (!response.isSuccessfully()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));


        Optional<ReviewReaction> optionalReviewReaction = reviewReactionRepository
                .findByReviewIdAndUserId(id, response.getUserId());

        ReviewReaction reviewReaction;
        if (optionalReviewReaction.isPresent()) {
            reviewReaction = optionalReviewReaction.get();
            if (reviewReaction.getReaction() == reactionType) {
                reviewReactionRepository.deleteByReviewIdAndUserId(id, response.getUserId());
                updateReactionCount(review, reactionType, -1);
            } else {
                reviewReaction.setReaction(reactionType);
                reviewReactionRepository.save(reviewReaction);
                updateReactionCount(review, reactionType, 1);
                updateReactionCount(review, inversionReaction(reactionType), -1);
            }
        } else {
            reviewReaction = ReviewReaction.builder()
                    .review(review)
                    .userId(response.getUserId())
                    .reaction(reactionType)
                    .build();
            updateReactionCount(review, reactionType, 1);
            reviewReactionRepository.save(reviewReaction);
        }

        reviewRepository.save(review);
    }

    private void updateReactionCount(Review review,
                             ReviewReaction.ReactionType reactionType,
                             Integer step) {
        if (reactionType == ReviewReaction.ReactionType.LIKE) {
            review.setLikes(review.getLikes() + step);
        } else {
            review.setDislikes(review.getDislikes() + step);
        }
    }

    private ReviewReaction.ReactionType inversionReaction (ReviewReaction.ReactionType reactionType) {
        if (reactionType == ReviewReaction.ReactionType.LIKE) {
            return ReviewReaction.ReactionType.DISLIKE;
        } else {
            return ReviewReaction.ReactionType.LIKE;
        }
    }
}
