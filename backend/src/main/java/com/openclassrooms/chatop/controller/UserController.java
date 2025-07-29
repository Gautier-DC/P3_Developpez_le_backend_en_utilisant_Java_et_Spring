package com.openclassrooms.chatop.controller;

import com.openclassrooms.chatop.dto.response.ErrorResponse;
import com.openclassrooms.chatop.dto.response.UserResponse;
import com.openclassrooms.chatop.service.AuthService;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * User controller handling user-related operations
 * Allows retrieval of user information by ID
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Users", description = "User information management endpoints")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AuthService userService;

    public UserController(AuthService userService) {
        this.userService = userService;
    }

    /**
     * Get user information by ID
     * Public endpoint to view user details (e.g., rental owner info)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID. Useful for displaying rental owner details.", tags = {
            "Users" }, security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class), examples = @ExampleObject(name = "User Details", value = """
                    {
                        "id": 2,
                        "name": "Owner Name",
                        "email": "test@test.com",
                        "created_at": "2022/02/02",
                        "updated_at": "2022/08/02"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = """
                    {
                        "message": "Unauthorized - Invalid token",
                        "code": "AUTH_401",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "User Not Found", value = """
                    {
                        "message": "User not found",
                        "code": "RESOURCE_404",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        logger.info("Request to get user with ID: {}", id);

        try {
            UserResponse user = userService.getUserById(id);
            logger.info("Successfully retrieved user: {}", user.getName());
            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("User not found with ID: {}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        } catch (Exception e) {
            logger.error("Error retrieving user with ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
}