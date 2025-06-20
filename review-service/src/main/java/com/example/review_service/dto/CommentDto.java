package com.example.review_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class Request {
        @NotBlank(message = "reviewId is required")
        private String reviewId;

        @NotBlank(message = "Text is required")
        @Size(min = 1, max = 1000, message = "Text must be between 1 and 1000 characters")
        private String text;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class Response {
        private String id;

        private String reviewId;

        private String userId;

        private String text;

        private String createdAt;
    }
}