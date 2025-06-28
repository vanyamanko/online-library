package com.example.book_service.controller;

import com.example.book_service.dto.CreateBookRequest;
import com.example.book_service.dto.UpdateBookRequest;
import com.example.book_service.model.Book;
import com.example.book_service.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteBookById_callsService() {
        doNothing().when(adminService).deleteBookById("id", "token");
        ResponseEntity<Void> response = adminController.deleteBookById("token", "id");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void createBook_returnsBook() {
        CreateBookRequest req = new CreateBookRequest();
        Book book = new Book();
        when(adminService.createBook("token", req)).thenReturn(book);
        ResponseEntity<Book> response = adminController.createBook("token", req);
        assertEquals(book, response.getBody());
    }

    @Test
    void updateBookById_returnsBook() {
        UpdateBookRequest req = new UpdateBookRequest();
        Book book = new Book();
        when(adminService.updateBookById("token", "id", req)).thenReturn(book);
        ResponseEntity<Book> response = adminController.updateBookById("token", "id", req);
        assertEquals(book, response.getBody());
    }
} 