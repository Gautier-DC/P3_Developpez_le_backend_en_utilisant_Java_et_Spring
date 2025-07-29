package com.openclassrooms.chatop.controller;

import com.openclassrooms.chatop.dto.request.LoginRequest;
import com.openclassrooms.chatop.dto.request.RegisterRequest;
import com.openclassrooms.chatop.dto.response.AuthResponse;
import com.openclassrooms.chatop.dto.response.ErrorResponse;
import com.openclassrooms.chatop.dto.response.UserResponse;
import com.openclassrooms.chatop.service.AuthService;
import com.openclassrooms.chatop.service.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

/**
 * Authentication controller handling user registration, login, and profile
 * endpoints
 * Provides public endpoints for registration/login and protected endpoint for
 * user profile
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "User authentication and profile management endpoints")
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
    @Operation(summary = "Register a new user", description = "Create a new user account with email, name, and password. Email must be unique.", tags = {
            "Authentication" })

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered and logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class), examples = @ExampleObject(name = "Successful Registration", value = """
                    {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "user": {
                            "id": 1,
                            "email": "john@example.com",
                            "name": "John Doe",
                            "created_at": "2025-01-15T10:30:00",
                            "updated_at": "2025-01-15T10:30:00"
                        }
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid registration data or email already exists", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Registration Error", value = """
                    {
                        "error": "Email already exists"
                    }
                    """)))
    })

    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request received for email: {}", request.getEmail());

        try {
            AuthResponse response = authService.register(request);
            logger.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("Registration failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        } catch (Exception e) {
            logger.error("Unexpected error during registration for email: {}", request.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Authenticate user and return JWT token
     * Public endpoint - no authentication required
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password, returns JWT token for API access.", tags = {
            "Authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class), examples = @ExampleObject(name = "Successful Login", value = """
                    {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Login Error", value = """
                    {
                        "message": "Invalid email or password",
                        "code": "AUTH_401",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """))),
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for email: {}", request.getEmail());

        try {
            AuthResponse response = loginService.login(request);
            logger.info("User logged in successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", request.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Get current authenticated user profile
     * Protected endpoint - requires valid JWT token
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieve profile information for the currently authenticated user.", tags = {
            "Authentication" }, security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class), examples = @ExampleObject(name = "User Profile", value = """
                    {
                        "id": 1,
                        "name": "John Doe",
                        "email": "john@example.com",
                        "created_at": "2024-01-15T10:30:00",
                        "updated_at": "2024-01-15T10:30:00"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Invalid or missing JWT token", content = @Content(examples = @ExampleObject(name = "Unauthorized", value = """
                    {
                        "error": "Unauthorized"
                    }
                    """)))
    })
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        logger.debug("Profile request received for user: {}", authentication.getName());

        try {
            String email = authentication.getName();
            UserResponse response = authService.getCurrentUser(email);
            logger.debug("Profile retrieved successfully for user: {}", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving profile for user: {}", authentication.getName(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "error: Unauthorized");
        }
    }
}