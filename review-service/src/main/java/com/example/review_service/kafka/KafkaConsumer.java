package com.example.review_service.kafka;

import com.example.review_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ReviewService reviewService;

    @KafkaListener(topics = "deleteBook", groupId = "reviewConsumer")
    public void listener(String bookId) {
       reviewService.deleteByBookId(bookId);
    }
}