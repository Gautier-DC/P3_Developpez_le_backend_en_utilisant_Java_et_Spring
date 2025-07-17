package com.openclassrooms.chatop.controller;

import com.openclassrooms.chatop.dto.request.LoginRequest;
import com.openclassrooms.chatop.dto.request.RegisterRequest;
import com.openclassrooms.chatop.dto.response.AuthResponse;
import com.openclassrooms.chatop.dto.response.UserResponse;
import com.openclassrooms.chatop.service.AuthService;
import com.openclassrooms.chatop.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Authentication controller handling user registration, login, and profile endpoints
 * Provides public endpoints for registration/login and protected endpoint for user profile
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final LoginService loginService;

    public AuthController(AuthService authService, LoginService loginService) {
        this.authService = authService;
        this.loginService = loginService;
    }

    /**
     * Register a new user
     * Public endpoint - no authentication required
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request received for email: {}", request.getEmail());
        
        try {
            AuthResponse response = authService.register(request);
            logger.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Registration failed for email: {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error during registration for email: {}", request.getEmail(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Authenticate user and return JWT token
     * Public endpoint - no authentication required
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for email: {}", request.getEmail());
        
        try {
            AuthResponse response = loginService.login(request);
            logger.info("User logged in successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", request.getEmail(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get current authenticated user profile
     * Protected endpoint - requires valid JWT token
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        logger.debug("Profile request received for user: {}", authentication.getName());
        
        try {
            String email = authentication.getName();
            UserResponse response = authService.getCurrentUser(email);
            logger.debug("Profile retrieved successfully for user: {}", email);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving profile for user: {}", authentication.getName(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}