package com.example.review_service.controller;

import com.example.review_service.dto.CommentDto;
import com.example.review_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/review/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto.Response> createComment(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CommentDto.Request createCommentRequest
    ) {
        return ResponseEntity.ok(commentService.createComment(token, createCommentRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id
    ) {
        commentService.deleteCommentById(token, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(params = "reviewId")
    public ResponseEntity<List<CommentDto.Response>> getAllByReviewId(@RequestParam String reviewId) {
        return ResponseEntity.ok(commentService.getAllByReviewId(reviewId));
    }
}