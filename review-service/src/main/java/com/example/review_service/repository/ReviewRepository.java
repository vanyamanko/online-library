package com.example.review_service.repository;

import com.example.review_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    boolean existsByUserId(String userId);
    void deleteByBookId(@Param("bookId") String bookId);
}