package com.example.review_service.service.impl;

import com.example.review_service.component.AuthComponent;
import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.dto.UpdateBookRatingRequest;
import com.example.review_service.dto.ValidationResponse;
import com.example.review_service.kafka.KafkaProduser;
import com.example.review_service.model.Review;
import com.example.review_service.repository.ReviewRepository;
import com.example.review_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthComponent authComponent;
    private final KafkaProduser kafkaProduser;

    public Review createReview(String token, CreateReviewRequest createReviewRequest) {
        ValidationResponse response = authComponent.validateToken(token);
        if (!response.isSuccessfully()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
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
}
