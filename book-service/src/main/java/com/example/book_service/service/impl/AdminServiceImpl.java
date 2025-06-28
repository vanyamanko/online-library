package com.example.book_service.service.impl;

import com.example.book_service.dto.CreateBookRequest;
import com.example.book_service.dto.UpdateBookRequest;
import com.example.book_service.kafka.KafkaProduser;
import com.example.book_service.model.Book;
import com.example.book_service.repository.BookRepository;
import com.example.book_service.service.AdminService;
import com.example.book_service.component.AuthComponent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final BookRepository bookRepository;
    private final AuthComponent validationService;
    private final KafkaProduser kafkaProduser;

    private void checkAdminAccess(String token) {
        if (!validationService.validateAdminAccess(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    @Transactional
    @Override
    public void deleteBookById(String id, String token) {
        checkAdminAccess(token);
        bookRepository.deleteById(id);
        kafkaProduser.deleteReviewByBookId(id);
    }

    @Override
    public Book createBook(String token, CreateBookRequest createBookRequest) {
        checkAdminAccess(token);
        Book book = Book.builder()
                .id("book_" + UUID.randomUUID().toString())
                .title(createBookRequest.getTitle())
                .authors(createBookRequest.getAuthors())
                .publisher(createBookRequest.getPublisher())
                .publishedDate(createBookRequest.getPublishedDate())
                .description(createBookRequest.getDescription())
                .pageCount(createBookRequest.getPageCount())
                .genres(createBookRequest.getGenres())
                .coverUrl(createBookRequest.getCoverUrl())
                .rating(0.0f)
                .reviewsCount(0)
                .build();

        return bookRepository.save(book);
    }

    @Transactional
    @Override
    public Book updateBookById(String token, String id, UpdateBookRequest updateBookRequest) {
        checkAdminAccess(token);
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        if (updateBookRequest.getTitle() != null) {
            existingBook.setTitle(updateBookRequest.getTitle());
        }
        if (updateBookRequest.getAuthors() != null) {
            existingBook.setAuthors(updateBookRequest.getAuthors());
        }
        if (updateBookRequest.getPublisher() != null) {
            existingBook.setPublisher(updateBookRequest.getPublisher());
        }
        if (updateBookRequest.getPublishedDate() != null) {
            existingBook.setPublishedDate(updateBookRequest.getPublishedDate());
        }
        if (updateBookRequest.getDescription() != null) {
            existingBook.setDescription(updateBookRequest.getDescription());
        }
        if (updateBookRequest.getPageCount() != null) {
            existingBook.setPageCount(updateBookRequest.getPageCount());
        }
        if (updateBookRequest.getGenres() != null) {
            existingBook.setGenres(updateBookRequest.getGenres());
        }
        if (updateBookRequest.getCoverUrl() != null) {
            existingBook.setCoverUrl(updateBookRequest.getCoverUrl());
        }
        if (updateBookRequest.getAverageRating() != null) {
            existingBook.setRating(updateBookRequest.getAverageRating());
        }
        if (updateBookRequest.getReviewsCount() != null) {
            existingBook.setReviewsCount(updateBookRequest.getReviewsCount());
        }

        return bookRepository.save(existingBook);
    }
}