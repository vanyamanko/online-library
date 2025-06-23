package com.example.book_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@JsonIgnoreProperties({"favoritedBy", "viewedBy"})
public class Book {
    @Id
    private String id;

    private String title;

    private Set<String> authors;

    private String publisher;

    private String publishedDate;

    private String description;

    private Integer pageCount;

    private Set<String> genres;

    private String coverUrl;

    private Float rating;

    private Integer reviewsCount;

    @ManyToMany(mappedBy = "favorite", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Personalization> favoritedBy;

    @ManyToMany(mappedBy = "viewHistory", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Personalization> viewedBy;
}