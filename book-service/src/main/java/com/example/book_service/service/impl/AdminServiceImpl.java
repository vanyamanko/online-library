package com.example.book_service.service.impl;

import com.example.book_service.repository.BookRepository;
import com.example.book_service.service.AdminService;
import com.example.book_service.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ValidationService validationService;
    private final BookRepository bookRepository;

    public void deleteBookById(String id, String token) {
        if (!validationService.validateAdminAccess(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
        bookRepository.deleteById(id);
    }
}