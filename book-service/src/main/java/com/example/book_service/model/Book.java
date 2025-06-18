package com.example.book_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Book {
    @Id
    private String id;

    private String title;

    private List<String> authors;

    private String publisher;

    private String publishedDate;

    private String description;

    private Integer pageCount;

    private List<String> genres;

    private String coverUrl;

    private Float rating;

    private Integer reviewsCount;
}