package com.example.review_service.kafka;

import com.example.review_service.dto.UpdateBookRatingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendToBookServiceAboutNewRating(UpdateBookRatingRequest updateBookRatingRequest) {
        kafkaTemplate.send("newReview", updateBookRatingRequest.toString());
    }
}
