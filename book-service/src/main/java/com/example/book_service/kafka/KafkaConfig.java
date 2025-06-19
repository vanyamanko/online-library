package com.example.book_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic newReview() {
        return new NewTopic(
                "deleteBook",
                1,
                (short) 1
        );
    }
}
