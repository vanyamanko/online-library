package com.example.book_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @Builder.Default
    private Set<String> authors = Collections.emptySet();

    private String publisher;

    private String publishedDate;

    private String description;

    private Integer pageCount;

    @Builder.Default
    private Set<String> genres = Collections.emptySet();

    private String coverUrl;
}
