package com.example.book_service.repository;

import com.example.book_service.model.Personalization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalizationRepository extends JpaRepository<Personalization, String> {
}