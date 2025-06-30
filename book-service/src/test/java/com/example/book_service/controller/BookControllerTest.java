package com.example.book_service.controller;

import com.example.book_service.model.Book;
import com.example.book_service.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookControllerTest {
    @Mock
    private BookService bookService;
    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooks_returnsList() {
        List<Book> books = List.of(new Book());
        when(bookService.getAllBooks()).thenReturn(books);
        ResponseEntity<List<Book>> response = bookController.getAllBooks();
        assertEquals(books, response.getBody());
    }

    @Test
    void getAllByAuthor_returnsList() {
        List<Book> books = List.of(new Book());
        when(bookService.getAllByAuthor("author")).thenReturn(books);
        ResponseEntity<List<Book>> response = bookController.getAllByAuthor("author");
        assertEquals(books, response.getBody());
    }

    @Test
    void getAllByTitle_returnsList() {
        List<Book> books = List.of(new Book());
        when(bookService.getAllByTitle("title")).thenReturn(books);
        ResponseEntity<List<Book>> response = bookController.getAllByTitle("title");
        assertEquals(books, response.getBody());
    }

    @Test
    void dislikeReview_callsService() {
        doNothing().when(bookService).toggleFavorite("token", "id");
        ResponseEntity<Void> response = bookController.dislikeReview("token", "id");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getFavorite_returnsSet() {
        Set<Book> books = Set.of(new Book());
        when(bookService.getFavoriteOrHistory("token", BookService.PersonalizationCategory.FAVORITE)).thenReturn(books);
        ResponseEntity<Set<Book>> response = bookController.getFavorite("token");
        assertEquals(books, response.getBody());
    }

    @Test
    void getHistory_returnsSet() {
        Set<Book> books = Set.of(new Book());
        when(bookService.getFavoriteOrHistory("token", BookService.PersonalizationCategory.HISTORY)).thenReturn(books);
        ResponseEntity<Set<Book>> response = bookController.getHistory("token");
        assertEquals(books, response.getBody());
    }

    @Test
    void getBookById_returnsBook() {
        Book book = new Book();
        when(bookService.getBookById("token", "id")).thenReturn(book);
        ResponseEntity<Book> response = bookController.getBookById("token", "id");
        assertEquals(book, response.getBody());
    }

    @Test
    void searchBooks_returnsList() {
        List<Book> books = List.of(new Book());
        when(bookService.findBooksByFilters(any())).thenReturn(books);
        ResponseEntity<List<Book>> response = bookController.searchBooks( null);
        assertEquals(books, response.getBody());
    }
} 