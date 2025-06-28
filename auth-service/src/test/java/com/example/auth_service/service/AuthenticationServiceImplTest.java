package com.example.auth_service.service;

import com.example.auth_service.dto.*;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationServiceImpl(userRepository, passwordEncoder, jwtService, authenticationManager, userDetailsService);
    }

    @Test
    void signup_ShouldReturnAuthResponse() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");
        User user = User.builder()
                .id("user_1")
                .username("testuser")
                .email("test@example.com")
                .password("encoded")
                .role(Role.USER)
                .joinedAt(Instant.now())
                .lastActive(Instant.now())
                .build();
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");
        AuthResponse response = authenticationService.signup(request);
        assertEquals("token", response.getToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    @Test
    void signin_ShouldReturnAuthResponse() {
        SignInRequest request = new SignInRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        User user = User.builder().username("testuser").password("encoded").role(Role.USER).build();
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");
        AuthResponse response = authenticationService.signin(request);
        assertEquals("token", response.getToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    @Test
    void refreshAccessToken_ShouldReturnAuthResponse() {
        User user = User.builder().username("testuser").role(Role.USER).build();
        when(jwtService.extractUsername(any())).thenReturn("testuser");
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(any(), eq(user))).thenReturn(true);
        when(jwtService.generateToken(eq(user))).thenReturn("accessToken");
        AuthResponse response = authenticationService.refreshAccessToken("Bearer refreshToken");
        assertEquals("accessToken", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void validateToken_ShouldReturnSuccess() {
        User user = User.builder().id("user_1").username("testuser").role(Role.USER).build();
        when(jwtService.extractUsername(any())).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername(eq("testuser"))).thenReturn(user);
        when(jwtService.isTokenValid(any(), eq(user))).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
        ValidationResponse response = authenticationService.validateToken("Bearer token");
        assertTrue(response.isSuccessfully());
        assertEquals(Role.USER, response.getRole());
        assertEquals("user_1", response.getUserId());
    }

    @Test
    void deleteUserById_ShouldCallRepository() {
        User user = User.builder().id("user_1").username("testuser").role(Role.ADMIN).build();
        ValidationResponse validationResponse = ValidationResponse.builder().successfully(true).role(Role.ADMIN).userId("user_1").build();
        when(jwtService.extractUsername(any())).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername(eq("testuser"))).thenReturn(user);
        when(jwtService.isTokenValid(any(), eq(user))).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(userRepository).deleteById("user_1");
        authenticationService.deleteUserById("Bearer token", "user_1");
        verify(userRepository, times(1)).deleteById("user_1");
    }

    @Test
    void deleteUserById_ShouldThrowException_WhenNotAllowed() {
        ValidationResponse validationResponse = ValidationResponse.builder().successfully(false).role(Role.USER).userId("user_2").build();
        when(jwtService.extractUsername(any())).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername(eq("testuser"))).thenReturn(null);
        when(jwtService.isTokenValid(any(), any())).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> authenticationService.deleteUserById("Bearer token", "user_2"));
    }
} 