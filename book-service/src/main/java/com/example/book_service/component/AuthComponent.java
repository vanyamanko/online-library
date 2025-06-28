package com.example.book_service.component;

import com.example.book_service.dto.ValidationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthComponent {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Value("${app.auth.validation-url}")
    private String validationUrl;

    public boolean validateAdminAccess(String token) {
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("token", token);
        ValidationResponse response = restTemplate.postForObject(
                validationUrl,
                requestBody,
                ValidationResponse.class
        );

        return response != null &&
                "ADMIN".equals(response.getRole()) &&
                response.isSuccessfully();
    }

    public ValidationResponse validateToken(String token) {
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("token", token);
        return restTemplate.postForObject(
                validationUrl,
                requestBody,
                ValidationResponse.class
        );
    }
}
