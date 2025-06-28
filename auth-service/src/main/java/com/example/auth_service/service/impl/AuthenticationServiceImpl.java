package com.example.auth_service.service.impl;

import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.dto.SignInRequest;
import com.example.auth_service.dto.SignUpRequest;
import com.example.auth_service.dto.ValidationResponse;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.AuthenticationService;
import com.example.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse signup(SignUpRequest request) {
        User user = User.builder()
                .id("user_" + UUID.randomUUID())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .joinedAt(Instant.now())
                .lastActive(Instant.now())
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    @Override
    public AuthResponse signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastActive(Instant.now());
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    @Override
    public AuthResponse refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new AccessDeniedException("Invalid token format");
        }

        String token = refreshToken.substring(7);

        final String username = jwtService.extractUsername(token);
        if (username == null) {
            throw new AccessDeniedException("Invalid refresh token");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        if (!jwtService.isTokenValid(token, user)) {
            throw new AccessDeniedException("Invalid or expired refresh token");
        }

        String accessToken = jwtService.generateToken(user);

        user.setLastActive(Instant.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(token)
                .build();
    }

    @Override
    public ValidationResponse validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ValidationResponse.builder()
                        .successfully(false)
                        .role(null)
                        .userId(null)
                        .build();
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String username = jwtService.extractUsername(token);
            if (username == null) {
                return ValidationResponse.builder()
                        .successfully(false)
                        .role(null)
                        .userId(null)
                        .build();
            }

            User user;
            try {
                user = (User) userDetailsService.loadUserByUsername(username);
            } catch (Exception e) {
                return ValidationResponse.builder()
                        .successfully(false)
                        .role(null)
                        .userId(null)
                        .build();
            }

            boolean isValid = jwtService.isTokenValid(token, user);

            if (!isValid) {
                return ValidationResponse.builder()
                        .successfully(false)
                        .role(null)
                        .userId(null)
                        .build();
            }

            user.setLastActive(Instant.now());
            userRepository.save(user);

            return ValidationResponse.builder()
                    .role(user.getRole())
                    .userId(user.getId())
                    .successfully(true)
                    .build();

        } catch (Exception e) {
            return ValidationResponse.builder()
                    .successfully(false)
                    .userId(null)
                    .role(null)
                    .build();
        }
    }

    @Override
    public void deleteUserById(String token, String id) {
        ValidationResponse response = validateToken(token);
        if (!response.isSuccessfully() || (response.getRole() != Role.ADMIN && !Objects.equals(response.getUserId(), id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        userRepository.deleteById(id);
    }

} 