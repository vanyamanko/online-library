package com.example.book_service.service;

import com.example.book_service.model.Book;
import com.example.book_service.model.Personalization;

import java.util.List;
import java.util.Set;

public interface BookService {
    List<Book> getAllBooks();

    List<Book> getAllByAuthor(String author);

    List<Book> getAllByTitle(String title);

    void updateRating(String id, Integer rating);

    void toggleFavorite(String token, String id);

    Set<Book> getFavoriteOrHistory(String token, PersonalizationCategory p);

    Book getBookById(String token, String id);

    enum PersonalizationCategory {
        FAVORITE, HISTORY
    }

}
