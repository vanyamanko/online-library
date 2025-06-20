package com.example.review_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    private ReactionType reaction;

    public enum ReactionType {
        LIKE, DISLIKE
    }
}