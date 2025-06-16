package com.example.book_service.controller;

import com.example.book_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books-update")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBookById(@RequestHeader("Authorization") String token, @PathVariable String id) {
        adminService.deleteBookById(id, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
