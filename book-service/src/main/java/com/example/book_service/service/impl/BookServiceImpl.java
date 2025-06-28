package com.example.book_service.service.impl;

import com.example.book_service.component.AuthComponent;
import com.example.book_service.dto.ValidationResponse;
import com.example.book_service.kafka.KafkaProduser;
import com.example.book_service.model.Book;
import com.example.book_service.model.Personalization;
import com.example.book_service.repository.BookRepository;
import com.example.book_service.repository.PersonalizationRepository;
import com.example.book_service.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final KafkaProduser kafkaProduser;
    private final AuthComponent authComponent;
    private final PersonalizationRepository personalizationRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> getAllByAuthor(String author) {
        return bookRepository.findAllByAuthor(author);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> getAllByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Transactional
    @Override
    public void updateRating(String id, Integer rating) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            Integer oldReviewsCount = book.getReviewsCount();
            Float newRating = (book.getRating() * oldReviewsCount + rating) / (oldReviewsCount + 1);
            book.setRating(newRating);
            book.setReviewsCount(oldReviewsCount + 1);
            bookRepository.save(book);
        } else {
            kafkaProduser.deleteReviewByBookId(id);
        }
    }

    @Transactional
    @Override
    public void toggleFavorite(String token, String id) {
        ValidationResponse response = authComponent.validateToken(token);
        if (!response.isSuccessfully()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        Personalization personalization = personalizationRepository.findById(response.getUserId())
                .orElseGet(() -> Personalization.builder()
                        .userId(response.getUserId())
                        .favorite(new HashSet<>())
                        .viewHistory(new LinkedHashSet<>())
                        .build());

        Set<Book> favorites = personalization.getFavorite();
        if (favorites == null) {
            favorites = new HashSet<>();
            personalization.setFavorite(favorites);
        }

        if (!favorites.remove(book)) {
            favorites.add(book);
        }

        personalizationRepository.save(personalization);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Book> getFavoriteOrHistory(String token, PersonalizationCategory p) {
        ValidationResponse response = authComponent.validateToken(token);
        if (!response.isSuccessfully()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        Personalization personalization = personalizationRepository.findById(response.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personalization not found"));

        if (p == PersonalizationCategory.FAVORITE) {
            return personalization.getFavorite();
        } else if (p == PersonalizationCategory.HISTORY) {
            return personalization.getViewHistory();
        } else {
            throw new IllegalArgumentException("PersonalizationCategory not found!");
        }
    }

    @Transactional
    @Override
    public Book getBookById(String token, String id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        ValidationResponse response = authComponent.validateToken(token);

        if (response.isSuccessfully()) {
            Personalization personalization = personalizationRepository.findById(response.getUserId())
                    .orElseGet(() -> Personalization.builder()
                            .userId(response.getUserId())
                            .favorite(new HashSet<>())
                            .viewHistory(new LinkedHashSet<>())
                            .build());

            Set<Book> history = personalization.getViewHistory();
            if (history == null) {
                history = new LinkedHashSet<>();
                personalization.setViewHistory(history);
            }

            Set<Book> updatedHistory = new LinkedHashSet<>(history);
            updatedHistory.add(book);

            if (updatedHistory.size() > 10) {
                updatedHistory = updatedHistory.stream()
                        .skip(updatedHistory.size() - 10)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }

            personalization.setViewHistory(updatedHistory);
            personalizationRepository.save(personalization);
        }

        return book;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findBooksByFilters(String genre, Float minRating, Float maxRating, String fromDate, String toDate) {
        return bookRepository.findBooksByFilters(
                genre,
                minRating,
                maxRating,
                fromDate,
                toDate);
    }


}
