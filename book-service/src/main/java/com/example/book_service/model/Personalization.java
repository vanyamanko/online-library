package com.example.book_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Personalization {
    @Id
    private String userId;

    @ManyToMany
    @JoinTable(
            name = "personalization_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Book> favorite = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "personalization_view_history",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Book> viewHistory = new LinkedHashSet<>();
}