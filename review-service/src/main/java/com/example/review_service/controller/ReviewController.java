package com.example.review_service.controller;

import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.model.Review;
import com.example.review_service.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<Review> createReview (
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CreateReviewRequest createReviewRequest
    ) {
        return ResponseEntity.ok(reviewService.createReview(token, createReviewRequest));
    }


}