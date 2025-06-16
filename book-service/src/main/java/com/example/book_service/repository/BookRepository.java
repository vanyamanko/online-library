package com.example.book_service.repository;

import com.example.book_service.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    @Query(value = "SELECT * FROM books b WHERE :author = ANY(b.authors)", nativeQuery = true)
    List<Book> findAllByAuthor(@Param("author") String author);
    List<Book> findByTitle(String title);
}