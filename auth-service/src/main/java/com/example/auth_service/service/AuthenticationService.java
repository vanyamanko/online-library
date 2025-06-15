package com.example.auth_service.service;

import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.dto.SignInRequest;
import com.example.auth_service.dto.SignUpRequest;

public interface AuthenticationService {
    AuthResponse signup(SignUpRequest request);
    AuthResponse signin(SignInRequest request);
    AuthResponse signinAdmin(SignInRequest request);
    AuthResponse refreshAccessToken(String refreshToken);
}