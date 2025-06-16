package com.example.book_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookRequest {
    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "authors is required")
    private List<String> authors;

    private String publisher;

    @NotBlank(message = "authors is required")
    private String publishedDate;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "pageCount is required")
    private Integer pageCount;

    @NotBlank(message = "genres is required")
    private List<String> genres;

    private String coverUrl;
}
