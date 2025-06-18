package com.example.review_service.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookRatingRequest {
    private String bookId;
    private Integer rating;
}