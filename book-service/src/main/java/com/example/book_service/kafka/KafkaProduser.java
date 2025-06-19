package com.example.book_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProduser {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void deleteReviewByBookId(String bookId) {
        kafkaTemplate.send("deleteBook", bookId);
    }
}
