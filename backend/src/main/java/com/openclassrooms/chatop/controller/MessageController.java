package com.openclassrooms.chatop.controller;

import com.openclassrooms.chatop.dto.request.MessageRequest;
import com.openclassrooms.chatop.dto.response.ErrorResponse;
import com.openclassrooms.chatop.dto.response.MessageResponse;
import com.openclassrooms.chatop.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Message controller handling messaging system
 * Allows users to send messages about rental properties
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Messages", description = "Messaging endpoints for users")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Send a new message about a rental
     * Anyone can send a message to a rental owner
     */
    @PostMapping
    @Operation(summary = "Send a new message", description = "Send a new message about a rental property.", tags = {
            "Messages" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class), examples = @ExampleObject(name = "Successful Registration", value = """
                    {
                        "message": "Message sent with success!"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid message data", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Message Error", value = """
                    {
                        "error": "Invalid message data"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Message Error", value = """
                    {
                        "error": "Unauthorized - Invalid token"
                    }
                    """)))
    })
    public ResponseEntity<Map<String, String>> sendMessage(@Valid @RequestBody MessageRequest request,
            Authentication authentication) {
        logger.info("Request to send message from user: {} about rental: {}",
                authentication.getName(), request.getRentalId());

        try {
            String userEmail = authentication.getName();
            messageService.sendMessage(request, userEmail);

            logger.info("Message sent successfully from user: {} about rental: {}",
                    userEmail, request.getRentalId());

            // Return simple success response
            Map<String, String> response = Map.of("message", "Message sent with success!");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid message data: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message data");
        } catch (RuntimeException e) {
            logger.error("Error sending message: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        } catch (Exception e) {
            logger.error("Error sending message from user: {}", authentication.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Get all messages for the authenticated user
     * Returns both sent and received messages
     */
    @GetMapping
    @Operation(summary = "Get all user messages", description = "Retrieve all messages for the authenticated user (sent and received).", tags = {
            "Messages" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class), examples = @ExampleObject(name = "Messages List", value = """
                    [
                        {
                            "user_id": 1,
                            "rental_id": 2,
                            "message": "Is this rental still available?"
                        },
                        {
                            "user_id": 2,
                            "rental_id": 1,
                            "message": "Thank you for your interest!"
                        }
                    ]
                    """))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Server Error", value = """
                    {
                        "message": "Internal server error",
                        "code": "SERVER_500",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """)))
    })
    public ResponseEntity<List<MessageResponse>> getUserMessages(Authentication authentication) {
        logger.info("Request to get messages for user: {}", authentication.getName());

        try {
            String userEmail = authentication.getName();
            List<MessageResponse> messages = messageService.getUserMessages(userEmail);

            logger.info("Successfully retrieved {} messages for user: {}",
                    messages.size(), userEmail);
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            logger.error("Error retrieving messages for user: {}", authentication.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

}