package com.example.book_service.service.impl;

import com.example.book_service.component.AuthComponent;
import com.example.book_service.dto.ValidationResponse;
import com.example.book_service.kafka.KafkaProduser;
import com.example.book_service.model.Book;
import com.example.book_service.model.Personalization;
import com.example.book_service.repository.BookRepository;
import com.example.book_service.repository.PersonalizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private KafkaProduser kafkaProduser;
    @Mock
    private AuthComponent authComponent;
    @Mock
    private PersonalizationRepository personalizationRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooks_returnsList() {
        List<Book> books = List.of(new Book());
        when(bookRepository.findAll()).thenReturn(books);
        assertEquals(books, bookService.getAllBooks());
    }

    @Test
    void getAllByAuthor_returnsList() {
        List<Book> books = List.of(new Book());
        when(bookRepository.findAllByAuthor("author")).thenReturn(books);
        assertEquals(books, bookService.getAllByAuthor("author"));
    }

    @Test
    void getAllByTitle_returnsList() {
        List<Book> books = List.of(new Book());
        when(bookRepository.findByTitle("title")).thenReturn(books);
        assertEquals(books, bookService.getAllByTitle("title"));
    }

    @Test
    void updateRating_existingBook_updatesRating() {
        Book book = Book.builder().rating(4.0f).reviewsCount(1).build();
        when(bookRepository.findById("id")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        bookService.updateRating("id", 5);
        verify(bookRepository).save(book);
        assertEquals(2, book.getReviewsCount());
    }

    @Test
    void updateRating_bookNotFound_callsKafka() {
        when(bookRepository.findById("id")).thenReturn(Optional.empty());
        bookService.updateRating("id", 5);
        verify(kafkaProduser).deleteReviewByBookId("id");
    }

    @Test
    void toggleFavorite_validToken_togglesFavorite() {
        ValidationResponse response = new ValidationResponse();
        response.setSuccessfully(true);
        response.setUserId("user");
        Book book = new Book();
        Personalization pers = Personalization.builder().userId("user").favorite(new HashSet<>()).viewHistory(new LinkedHashSet<>()).build();
        when(authComponent.validateToken("token")).thenReturn(response);
        when(bookRepository.findById("id")).thenReturn(Optional.of(book));
        when(personalizationRepository.findById("user")).thenReturn(Optional.of(pers));
        when(personalizationRepository.save(any())).thenReturn(pers);
        assertDoesNotThrow(() -> bookService.toggleFavorite("token", "id"));
    }

    @Test
    void toggleFavorite_invalidToken_throws() {
        ValidationResponse response = new ValidationResponse();
        response.setSuccessfully(false);
        when(authComponent.validateToken("token")).thenReturn(response);
        assertThrows(ResponseStatusException.class, () -> bookService.toggleFavorite("token", "id"));
    }

    @Test
    void getFavoriteOrHistory_returnsFavorite() {
        ValidationResponse response = new ValidationResponse();
        response.setSuccessfully(true);
        response.setUserId("user");
        Personalization pers = Personalization.builder().userId("user").favorite(Set.of(new Book())).viewHistory(Set.of()).build();
        when(authComponent.validateToken("token")).thenReturn(response);
        when(personalizationRepository.findById("user")).thenReturn(Optional.of(pers));
        assertEquals(pers.getFavorite(), bookService.getFavoriteOrHistory("token", BookServiceImpl.PersonalizationCategory.FAVORITE));
    }

    @Test
    void getFavoriteOrHistory_invalidToken_throws() {
        ValidationResponse response = new ValidationResponse();
        response.setSuccessfully(false);
        when(authComponent.validateToken("token")).thenReturn(response);
        assertThrows(ResponseStatusException.class, () -> bookService.getFavoriteOrHistory("token", BookServiceImpl.PersonalizationCategory.FAVORITE));
    }

    @Test
    void getBookById_validToken_addsToHistory() {
        Book book = new Book();
        ValidationResponse response = new ValidationResponse();
        response.setSuccessfully(true);
        response.setUserId("user");
        Personalization pers = Personalization.builder().userId("user").favorite(new HashSet<>()).viewHistory(new LinkedHashSet<>()).build();
        when(bookRepository.findById("id")).thenReturn(Optional.of(book));
        when(authComponent.validateToken("token")).thenReturn(response);
        when(personalizationRepository.findById("user")).thenReturn(Optional.of(pers));
        when(personalizationRepository.save(any())).thenReturn(pers);
        assertEquals(book, bookService.getBookById("token", "id"));
    }

    @Test
    void findBooksByFilters_returnsList() {
        List<Book> books = List.of(new Book());
        when(bookRepository.findBooksByFilters(any(), any(), any(), any(), any())).thenReturn(books);
        assertEquals(books, bookService.findBooksByFilters(null, null, null, null, null));
    }
} 