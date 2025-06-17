package com.example.book_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookRequest {
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private Integer pageCount;
    private List<String> genres;
    private String coverUrl;
    private Float averageRating;
    private Integer reviewsCount;
} 