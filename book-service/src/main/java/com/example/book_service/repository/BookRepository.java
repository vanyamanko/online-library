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

    @Query(value = "SELECT * FROM book b WHERE " +
            "(:genre IS NULL OR :genre = '' OR :genre = ANY(b.genres)) AND " +
            "(:minRating IS NULL OR b.rating >= :minRating) AND " +
            "(:maxRating IS NULL OR b.rating <= :maxRating) AND " +
            "(:fromDate IS NULL OR b.published_date >= :fromDate) AND " +
            "(:toDate IS NULL OR b.published_date <= :toDate)",
            nativeQuery = true)
    List<Book> findBooksByFilters(
            @Param("genre") String genre,
            @Param("minRating") Float minRating,
            @Param("maxRating") Float maxRating,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate);
}