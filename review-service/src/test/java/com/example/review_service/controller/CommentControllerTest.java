package com.example.review_service.controller;

import com.example.review_service.dto.CommentDto;
import com.example.review_service.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createComment_shouldReturnOk() throws Exception {
        CommentDto.Request req = new CommentDto.Request();
        req.setReviewId("review1");
        req.setText("text");
        CommentDto.Response resp = CommentDto.Response.builder()
                .id("comm1").reviewId("review1").userId("user1").text("text").createdAt("now").build();
        when(commentService.createComment(anyString(), any(CommentDto.Request.class))).thenReturn(resp);
        mockMvc.perform(post("/api/v1/review/comment")
                .header("Authorization", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("comm1"));
    }

    @Test
    void deleteCommentById_shouldReturnNoContent() throws Exception {
        doNothing().when(commentService).deleteCommentById(anyString(), anyString());
        mockMvc.perform(delete("/api/v1/review/comment/comm1")
                .header("Authorization", "token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllByReviewId_shouldReturnOk() throws Exception {
        CommentDto.Response resp = CommentDto.Response.builder()
                .id("comm1").reviewId("review1").userId("user1").text("text").createdAt("now").build();
        when(commentService.getAllByReviewId(eq("review1"))).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/v1/review/comment")
                .param("reviewId", "review1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("comm1"));
    }
} 