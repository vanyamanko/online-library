package com.example.book_service.service;

import com.example.book_service.model.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();

    List<Book> getAllByAuthor(String author);

    List<Book> getAllByTitle(String title);

    void updateRating(String id, Integer rating);
}
