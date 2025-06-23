package com.example.auth_service.service;

import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.dto.SignInRequest;
import com.example.auth_service.dto.SignUpRequest;
import com.example.auth_service.dto.ValidationResponse;

public interface AuthenticationService {
    AuthResponse signup(SignUpRequest request);
    AuthResponse signin(SignInRequest request);
    AuthResponse refreshAccessToken(String refreshToken);
    ValidationResponse validateToken(String token);
}