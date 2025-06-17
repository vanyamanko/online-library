package com.example.book_service.service;

import com.example.book_service.dto.CreateBookRequest;
import com.example.book_service.dto.UpdateBookRequest;
import com.example.book_service.model.Book;

public interface AdminService {
    public void deleteBookById(String id, String token);
    public Book createBook(String token, CreateBookRequest createBookRequest);
    public Book updateBookById(String token, String id, UpdateBookRequest updateBookRequest);
}
