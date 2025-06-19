package com.example.book_service.service.impl;

import com.example.book_service.kafka.KafkaProduser;
import com.example.book_service.model.Book;
import com.example.book_service.repository.BookRepository;
import com.example.book_service.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final KafkaProduser kafkaProduser;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAllByAuthor(String author) {
        return bookRepository.findAllByAuthor(author);
    }

    public List<Book> getAllByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

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
}