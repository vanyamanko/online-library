package com.example.review_service.service.impl;

import com.example.review_service.component.AuthComponent;
import com.example.review_service.dto.CommentDto;
import com.example.review_service.dto.ValidationResponse;
import com.example.review_service.model.Comment;
import com.example.review_service.model.Review;
import com.example.review_service.repository.CommentRepository;
import com.example.review_service.repository.ReviewRepository;
import com.example.review_service.service.utility.CommentUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private AuthComponent authComponent;
    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComment_invalidToken() {
        String token = "token";
        CommentDto.Request request = new CommentDto.Request();
        request.setReviewId("review1");
        request.setText("test comment");
        when(authComponent.validateToken(token)).thenReturn(new ValidationResponse(null, null, false));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                commentService.createComment(token, request)
        );
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void createComment_reviewNotFound() {
        String token = "token";
        String userId = "user1";
        CommentDto.Request request = new CommentDto.Request();
        request.setReviewId("review1");
        request.setText("test comment");
        when(authComponent.validateToken(token)).thenReturn(new ValidationResponse(userId, "USER", true));
        when(reviewRepository.findById("review1")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                commentService.createComment(token, request)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void deleteCommentById_owner() {
        String token = "token";
        String userId = "user1";
        String commentId = "comm1";
        Comment comment = Comment.builder().id(commentId).userId(userId).review(Review.builder().id("review1").build()).build();
        when(authComponent.validateToken(token)).thenReturn(new ValidationResponse(userId, "USER", true));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);
        assertDoesNotThrow(() -> commentService.deleteCommentById(token, commentId));
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void deleteCommentById_admin() {
        String token = "token";
        String userId = "admin";
        String commentId = "comm1";
        Comment comment = Comment.builder().id(commentId).userId("user2").review(Review.builder().id("review1").build()).build();
        when(authComponent.validateToken(token)).thenReturn(new ValidationResponse(userId, "ADMIN", true));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);
        assertDoesNotThrow(() -> commentService.deleteCommentById(token, commentId));
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void deleteCommentById_forbidden() {
        String token = "token";
        String userId = "user1";
        String commentId = "comm1";
        Comment comment = Comment.builder().id(commentId).userId("user2").review(Review.builder().id("review1").build()).build();
        when(authComponent.validateToken(token)).thenReturn(new ValidationResponse(userId, "USER", true));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                commentService.deleteCommentById(token, commentId)
        );
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void deleteCommentById_notFound() {
        String token = "token";
        String userId = "user1";
        String commentId = "comm1";
        when(authComponent.validateToken(token)).thenReturn(new ValidationResponse(userId, "USER", true));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                commentService.deleteCommentById(token, commentId)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getAllByReviewId_success() {
        String reviewId = "review1";
        Comment comment = Comment.builder().id("comm1").review(Review.builder().id(reviewId).build()).userId("user1").text("text").createdAt("now").build();
        when(commentRepository.findAllByReviewId(reviewId)).thenReturn(List.of(comment));
        List<CommentDto.Response> responses = commentService.getAllByReviewId(reviewId);
        assertEquals(1, responses.size());
        assertEquals(comment.getId(), responses.get(0).getId());
        assertEquals(comment.getText(), responses.get(0).getText());
        assertEquals(comment.getUserId(), responses.get(0).getUserId());
        assertEquals(comment.getCreatedAt(), responses.get(0).getCreatedAt());
        assertEquals(reviewId, responses.get(0).getReviewId());
    }
} 