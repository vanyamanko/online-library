package com.example.book_service.controller;

import com.example.book_service.dto.CreateBookRequest;
import com.example.book_service.dto.UpdateBookRequest;
import com.example.book_service.model.Book;
import com.example.book_service.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books-update")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id
    ) {
        adminService.deleteBookById(id, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CreateBookRequest createBookRequest
    ) {
        return ResponseEntity.ok(adminService.createBook(token, createBookRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBookById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @RequestBody UpdateBookRequest updateBookRequest
    ) {
        return ResponseEntity.ok(adminService.updateBookById(token, id, updateBookRequest));
    }
}
