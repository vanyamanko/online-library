package com.example.book_service.dto;

import lombok.Builder;

@Builder
public record FilterBookRequest(
        String genre,
        Float minRating,
        Float maxRating,
        String fromDate,
        String toDate
) {
}