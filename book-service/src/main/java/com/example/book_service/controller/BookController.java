package com.example.book_service.controller;

import com.example.book_service.model.Book;
import com.example.book_service.model.Personalization;
import com.example.book_service.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping(params = "author")
    public ResponseEntity<List<Book>> getAllByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.getAllByAuthor(author));
    }

    @GetMapping(params = "title")
    public ResponseEntity<List<Book>> getAllByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.getAllByTitle(title));
    }

    @PostMapping("/favorite/{id}")
    public ResponseEntity<Void> dislikeReview(@RequestHeader("Authorization") String token, @PathVariable String id) {
        bookService.toggleFavorite(token, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/my-favorite")
    public ResponseEntity<Set<Book>> getFavorite(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(bookService.getFavoriteOrHistory(token, BookService.PersonalizationCategory.FAVORITE));
    }

    @GetMapping("/my-history")
    public ResponseEntity<Set<Book>> getHistory(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(bookService.getFavoriteOrHistory(token, BookService.PersonalizationCategory.HISTORY));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(token, id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false) Float maxRating,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        return ResponseEntity.ok(bookService.findBooksByFilters(
                genre, minRating, maxRating, fromDate, toDate));
    }
}
