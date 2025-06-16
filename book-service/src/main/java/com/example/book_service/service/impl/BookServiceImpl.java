package com.example.book_service.service.impl;

import com.example.book_service.model.Book;
import com.example.book_service.repository.BookRepository;
import com.example.book_service.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAllByAuthor(String author) {
        return bookRepository.findAllByAuthor(author);
    }

    public List<Book> getAllByTitle(String title) {
        return bookRepository.findByTitle(title);
    }
} 