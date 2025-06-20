package com.example.review_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {
    @NotBlank(message = "bookId is required")
    private String bookId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Text is required")
    @Size(min = 1, max = 10000, message = "Text must be between 1 and 1000 characters")
    private String text;
}