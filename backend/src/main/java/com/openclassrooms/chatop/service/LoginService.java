package com.openclassrooms.chatop.service;

import com.openclassrooms.chatop.dto.request.LoginRequest;
import com.openclassrooms.chatop.dto.response.AuthResponse;
import com.openclassrooms.chatop.entity.User;
import com.openclassrooms.chatop.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service dedicated to handling login operations
 * Separated from AuthService to avoid circular dependencies
 */
@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginService(AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Authenticate user and return JWT token
     * Validates credentials and generates access token
     */
    public AuthResponse login(LoginRequest request) {
        logger.info("Attempting to login user with email: {}", request.getEmail());

        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            logger.info("Authentication successful for: {}", request.getEmail());

            // Find user in database for response (AFTER successful authentication)
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT token using email (not User object to avoid cast issues)
            String token = jwtService.generateToken(request.getEmail());

            logger.info("User logged in successfully: {}", request.getEmail());

            // Use constructor that takes (String token, User user)
            return new AuthResponse(token, user);

        } catch (Exception e) {
            logger.error("Login failed for user: {} - {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Invalid credentials");
        }
    }
}