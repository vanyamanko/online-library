package com.example.review_service.controller;

import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.model.Review;
import com.example.review_service.model.ReviewReaction;
import com.example.review_service.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CreateReviewRequest createReviewRequest
    ) {
        return ResponseEntity.ok(reviewService.createReview(token, createReviewRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReviewById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id
    ) {
        reviewService.deleteReviewById(token, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<Void> likeReview(@RequestHeader("Authorization") String token, @PathVariable String id) {
        reviewService.reviewReaction(token, id, ReviewReaction.ReactionType.LIKE);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/dislike/{id}")
    public ResponseEntity<Void> dislikeReview(@RequestHeader("Authorization") String token, @PathVariable String id) {
        reviewService.reviewReaction(token, id, ReviewReaction.ReactionType.DISLIKE);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}