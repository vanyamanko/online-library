package com.example.book_service.service;

import com.example.book_service.model.Book;

import java.util.List;

public interface BookService {
    public List<Book> getAllBooks();

    public List<Book> getAllByAuthor(String author);

    public List<Book> getAllByTitle(String title);
}
