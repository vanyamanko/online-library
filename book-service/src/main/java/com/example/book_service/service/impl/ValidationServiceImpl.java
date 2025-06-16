package com.example.book_service.service.impl;

import com.example.book_service.dto.ValidationResponse;
import com.example.book_service.service.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private static final String VALIDATION_URL = "http://localhost:8080/api/v1/auth/validate";

    public boolean validateAdminAccess(String token) {
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("token", token);
        ValidationResponse response = restTemplate.postForObject(
                VALIDATION_URL,
                requestBody,
                ValidationResponse.class
        );

        return response != null &&
                "ADMIN".equals(response.getRole()) &&
                response.isSuccessfully();
    }
}