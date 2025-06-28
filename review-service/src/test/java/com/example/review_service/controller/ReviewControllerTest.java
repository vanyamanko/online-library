package com.example.review_service.controller;

import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.model.Review;
import com.example.review_service.model.ReviewReaction;
import com.example.review_service.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReviewService reviewService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReview_shouldReturnOk() throws Exception {
        CreateReviewRequest req = CreateReviewRequest.builder()
                .bookId("book1").rating(5).text("text").build();
        Review review = Review.builder().id("review1").bookId("book1").userId("user1").rating(5).text("text").build();
        when(reviewService.createReview(anyString(), any(CreateReviewRequest.class))).thenReturn(review);
        mockMvc.perform(post("/api/v1/review")
                .header("Authorization", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("review1"));
    }

    @Test
    void deleteReviewById_shouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).deleteReviewById(anyString(), anyString());
        mockMvc.perform(delete("/api/v1/review/review1")
                .header("Authorization", "token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void likeReview_shouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).reviewReaction(anyString(), anyString(), eq(ReviewReaction.ReactionType.LIKE));
        mockMvc.perform(post("/api/v1/review/like/review1")
                .header("Authorization", "token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void dislikeReview_shouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).reviewReaction(anyString(), anyString(), eq(ReviewReaction.ReactionType.DISLIKE));
        mockMvc.perform(post("/api/v1/review/dislike/review1")
                .header("Authorization", "token"))
                .andExpect(status().isNoContent());
    }
} 