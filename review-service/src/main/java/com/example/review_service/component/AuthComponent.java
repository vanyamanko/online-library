package com.example.review_service.component;

import com.example.review_service.dto.ValidationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthComponent {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private static final String VALIDATION_URL = "http://localhost:8080/api/v1/auth/validate";

    public ValidationResponse validateToken(String token) {
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("token", token);
        return restTemplate.postForObject(
                VALIDATION_URL,
                requestBody,
                ValidationResponse.class
        );
    }
}
