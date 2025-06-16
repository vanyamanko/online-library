package com.example.book_service.controller;

import com.example.book_service.model.Book;
import com.example.book_service.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
