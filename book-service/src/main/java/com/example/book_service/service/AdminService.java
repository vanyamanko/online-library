package com.example.book_service.service;

import com.example.book_service.dto.CreateBookRequest;
import com.example.book_service.dto.UpdateBookRequest;
import com.example.book_service.model.Book;

public interface AdminService {
    void deleteBookById(String id, String token);

    Book createBook(String token, CreateBookRequest createBookRequest);

    Book updateBookById(String token, String id, UpdateBookRequest updateBookRequest);
}
