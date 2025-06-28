package com.example.review_service.service.impl;

import com.example.review_service.component.AuthComponent;
import com.example.review_service.dto.CommentDto;
import com.example.review_service.dto.ValidationResponse;
import com.example.review_service.model.Comment;
import com.example.review_service.model.Review;
import com.example.review_service.repository.CommentRepository;
import com.example.review_service.repository.ReviewRepository;
import com.example.review_service.service.CommentService;
import com.example.review_service.service.utility.CommentUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final AuthComponent authComponent;

    @Transactional
    @Override
    public CommentDto.Response createComment(String token, CommentDto.Request createCommentRequest) {

        ValidationResponse response = authComponent.validateToken(token);
        if (!response.isSuccessfully()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        Review review = reviewRepository.findById(createCommentRequest.getReviewId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        Comment comment = Comment.builder()
                .id("comm_" + UUID.randomUUID())
                .createdAt(Instant.now().toString())
                .text(createCommentRequest.getText())
                .userId(response.getUserId())
                .review(review)
                .build();
        commentRepository.save(comment);

        return CommentUtils.buildCommentDtoFromModel(comment);
    }

    @Transactional
    @Override
    public void deleteCommentById(String token, String id) {
        ValidationResponse response = authComponent.validateToken(token);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (response.isSuccessfully() &&
                (response.getRole().equals("ADMIN") || response.getUserId().equals(comment.getUserId()))
        ) {
            commentRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto.Response> getAllByReviewId(String reviewId) {
        List<Comment> comments = commentRepository.findAllByReviewId(reviewId);
        return comments.stream()
                .map(CommentUtils::buildCommentDtoFromModel).toList();
    }
}