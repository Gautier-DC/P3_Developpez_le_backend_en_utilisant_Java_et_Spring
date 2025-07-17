package com.openclassrooms.chatop.service;

import com.openclassrooms.chatop.dto.request.RegisterRequest;
import com.openclassrooms.chatop.dto.response.AuthResponse;
import com.openclassrooms.chatop.dto.response.UserResponse;
import com.openclassrooms.chatop.entity.User;
import com.openclassrooms.chatop.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Authentication service handling user registration and user details loading
 * Implements UserDetailsService for Spring Security integration
 */
@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user in the system
     * Validates email uniqueness and creates user with encoded password
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Attempting to register user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save user to database
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate JWT token
        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(token, savedUser);
    }

    /**
     * Get current user information by email
     * Used for protected endpoints to return user profile
     */
    public UserResponse getCurrentUser(String email) {
        logger.debug("Fetching current user info for email: {}", email);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    /**
     * Implementation of UserDetailsService interface
     * Used by Spring Security for authentication
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email: {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User user = userOptional.get();
        logger.debug("User loaded successfully: {}", email);

        // Return Spring Security UserDetails object
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(new ArrayList<>()) // Empty authorities for now
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
}