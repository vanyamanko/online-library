package com.example.review_service.service.impl;

import com.example.review_service.component.AuthComponent;
import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.dto.UpdateBookRatingRequest;
import com.example.review_service.dto.ValidationResponse;
import com.example.review_service.kafka.KafkaProducer;
import com.example.review_service.model.Review;
import com.example.review_service.model.ReviewReaction;
import com.example.review_service.repository.ReviewReactionRepository;
import com.example.review_service.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewReactionRepository reviewReactionRepository;

    @Mock
    private AuthComponent authComponent;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private String validToken;
    private String userId;
    private String bookId;
    private String reviewId;
    private ValidationResponse validationResponse;
    private CreateReviewRequest createReviewRequest;
    private Review review;

    @BeforeEach
    void setUp() {
        validToken = "valid-token";
        userId = "user123";
        bookId = "book456";
        reviewId = "review_789";

        validationResponse = ValidationResponse.builder()
                .successfully(true)
                .userId(userId)
                .role("USER")
                .build();

        createReviewRequest = CreateReviewRequest.builder()
                .bookId(bookId)
                .rating(5)
                .text("Great book!")
                .build();

        review = Review.builder()
                .id(reviewId)
                .rating(5)
                .text("Great book!")
                .bookId(bookId)
                .userId(userId)
                .comments(new ArrayList<>())
                .likes(0)
                .dislikes(0)
                .createdAt(Instant.now().toString())
                .build();
    }

    @Test
    void createReview_WithValidToken_ShouldCreateReview() {
        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.createReview(validToken, createReviewRequest);

        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals(5, result.getRating());
        assertEquals("Great book!", result.getText());
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getLikes());
        assertEquals(0, result.getDislikes());

        verify(reviewRepository).save(any(Review.class));
        verify(kafkaProducer).sendToBookServiceAboutNewRating(any(UpdateBookRatingRequest.class));
    }

    @Test
    void createReview_WithInvalidToken_ShouldThrowForbiddenException() {
        ValidationResponse invalidResponse = ValidationResponse.builder()
                .successfully(false)
                .build();
        when(authComponent.validateToken(validToken)).thenReturn(invalidResponse);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(validToken, createReviewRequest));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access denied", exception.getReason());

        verify(reviewRepository, never()).save(any());
        verify(kafkaProducer, never()).sendToBookServiceAboutNewRating(any());
    }

    @Test
    void createReview_WhenReviewAlreadyExists_ShouldThrowDataIntegrityViolationException() {
        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
                () -> reviewService.createReview(validToken, createReviewRequest));

        assertEquals("There is already a review", exception.getMessage());

        verify(reviewRepository, never()).save(any());
        verify(kafkaProducer, never()).sendToBookServiceAboutNewRating(any());
    }


    @Test
    void deleteByBookId_ShouldCallRepositoryDeleteMethod() {
        reviewService.deleteByBookId(bookId);
        verify(reviewRepository).deleteByBookId(bookId);
    }

    @Test
    void deleteReviewById_WithValidTokenAndOwner_ShouldDeleteReview() {
        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReviewById(validToken, reviewId);

        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void deleteReviewById_WithAdminRole_ShouldDeleteReview() {
        ValidationResponse adminResponse = ValidationResponse.builder()
                .successfully(true)
                .userId("admin123")
                .role("ADMIN")
                .build();
        when(authComponent.validateToken(validToken)).thenReturn(adminResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReviewById(validToken, reviewId);

        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void deleteReviewById_WithInvalidToken_ShouldThrowForbiddenException() {
        ValidationResponse invalidResponse = ValidationResponse.builder()
                .successfully(false)
                .build();
        when(authComponent.validateToken(validToken)).thenReturn(invalidResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.deleteReviewById(validToken, reviewId));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access denied", exception.getReason());

        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void deleteReviewById_WithNonOwnerNonAdmin_ShouldThrowForbiddenException() {
        ValidationResponse otherUserResponse = ValidationResponse.builder()
                .successfully(true)
                .userId("otherUser123")
                .role("USER")
                .build();
        when(authComponent.validateToken(validToken)).thenReturn(otherUserResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.deleteReviewById(validToken, reviewId));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access denied", exception.getReason());

        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void deleteReviewById_WithNonExistentReview_ShouldThrowNotFoundException() {
        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.deleteReviewById(validToken, reviewId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Review not found", exception.getReason());
    }

    @Test
    void reviewReaction_WithNewLike_ShouldCreateReactionAndIncrementLikes() {
        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewReactionRepository.findByReviewIdAndUserId(reviewId, userId))
                .thenReturn(Optional.empty());

        reviewService.reviewReaction(validToken, reviewId, ReviewReaction.ReactionType.LIKE);

        assertEquals(1, review.getLikes());
        verify(reviewReactionRepository).save(any(ReviewReaction.class));
        verify(reviewRepository).save(review);
    }

    @Test
    void reviewReaction_WithNewDislike_ShouldCreateReactionAndIncrementDislikes() {
        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewReactionRepository.findByReviewIdAndUserId(reviewId, userId))
                .thenReturn(Optional.empty());

        reviewService.reviewReaction(validToken, reviewId, ReviewReaction.ReactionType.DISLIKE);

        assertEquals(1, review.getDislikes());
        verify(reviewReactionRepository).save(any(ReviewReaction.class));
        verify(reviewRepository).save(review);
    }

    @Test
    void reviewReaction_WithExistingSameReaction_ShouldRemoveReaction() {
        review.setLikes(1);
        ReviewReaction existingReaction = ReviewReaction.builder()
                .review(review)
                .userId(userId)
                .reaction(ReviewReaction.ReactionType.LIKE)
                .build();

        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewReactionRepository.findByReviewIdAndUserId(reviewId, userId))
                .thenReturn(Optional.of(existingReaction));

        reviewService.reviewReaction(validToken, reviewId, ReviewReaction.ReactionType.LIKE);

        assertEquals(0, review.getLikes());
        verify(reviewReactionRepository).deleteByReviewIdAndUserId(reviewId, userId);
        verify(reviewRepository).save(review);
    }

    @Test
    void reviewReaction_WithExistingDifferentReaction_ShouldUpdateReaction() {
        review.setLikes(1);
        review.setDislikes(0);
        ReviewReaction existingReaction = ReviewReaction.builder()
                .review(review)
                .userId(userId)
                .reaction(ReviewReaction.ReactionType.LIKE)
                .build();

        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewReactionRepository.findByReviewIdAndUserId(reviewId, userId))
                .thenReturn(Optional.of(existingReaction));

        reviewService.reviewReaction(validToken, reviewId, ReviewReaction.ReactionType.DISLIKE);

        assertEquals(0, review.getLikes());
        assertEquals(1, review.getDislikes());
        assertEquals(ReviewReaction.ReactionType.DISLIKE, existingReaction.getReaction());
        verify(reviewReactionRepository).save(existingReaction);
        verify(reviewRepository).save(review);
    }

    @Test
    void reviewReaction_WithInvalidToken_ShouldThrowForbiddenException() {
        ValidationResponse invalidResponse = ValidationResponse.builder()
                .successfully(false)
                .build();
        when(authComponent.validateToken(validToken)).thenReturn(invalidResponse);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.reviewReaction(validToken, reviewId, ReviewReaction.ReactionType.LIKE));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access denied", exception.getReason());

        verify(reviewReactionRepository, never()).save(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void reviewReaction_WithNonExistentReview_ShouldThrowNotFoundException() {
        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.reviewReaction(validToken, reviewId, ReviewReaction.ReactionType.LIKE));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Review not found", exception.getReason());
    }

    @Test
    void reviewReaction_ShouldCorrectlyHandleReactionCounts() {
        review.setLikes(5);
        review.setDislikes(3);

        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewReactionRepository.findByReviewIdAndUserId(reviewId, userId))
                .thenReturn(Optional.empty());

        reviewService.reviewReaction(validToken, reviewId, ReviewReaction.ReactionType.LIKE);

        assertEquals(6, review.getLikes());
        assertEquals(3, review.getDislikes());
    }

    @Test
    void reviewReaction_ShouldHandleReactionToggling() {
        review.setLikes(1);
        review.setDislikes(0);
        ReviewReaction existingLike = ReviewReaction.builder()
                .review(review)
                .userId(userId)
                .reaction(ReviewReaction.ReactionType.LIKE)
                .build();

        when(authComponent.validateToken(validToken)).thenReturn(validationResponse);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewReactionRepository.findByReviewIdAndUserId(reviewId, userId))
                .thenReturn(Optional.of(existingLike));

        reviewService.reviewReaction(validToken, reviewId, ReviewReaction.ReactionType.DISLIKE);

        assertEquals(0, review.getLikes());
        assertEquals(1, review.getDislikes());
        assertEquals(ReviewReaction.ReactionType.DISLIKE, existingLike.getReaction());
    }
}