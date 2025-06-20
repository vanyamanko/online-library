package com.example.review_service.service;

import com.example.review_service.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto.Response createComment(String token, CommentDto.Request createCommentRequest);
    void deleteCommentById(String token, String id);
    List<CommentDto.Response> getAllByReviewId(String reviewId);
}
