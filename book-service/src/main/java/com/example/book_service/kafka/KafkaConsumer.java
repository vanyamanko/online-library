package com.example.book_service.kafka;

import com.example.book_service.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private static final Pattern RATING_PATTERN = Pattern.compile(
            "UpdateBookRatingRequest\\(bookId=(.*?), rating=(\\d+)\\)"
    );

    private final BookService bookService;

    @KafkaListener(topics = "newReview", groupId = "bookConsumer")
    public void listener(String message) {
        Matcher matcher = RATING_PATTERN.matcher(message);

        if (!matcher.find()) {
            return;
        }
        String bookId = matcher.group(1);
        int ratingValue = Integer.parseInt(matcher.group(2));

        bookService.updateRating(bookId, ratingValue);
    }
}