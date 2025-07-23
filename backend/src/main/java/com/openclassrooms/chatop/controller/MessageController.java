package com.openclassrooms.chatop.controller;

import com.openclassrooms.chatop.dto.request.MessageRequest;
import com.openclassrooms.chatop.dto.response.MessageResponse;
import com.openclassrooms.chatop.service.MessageService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

/**
 * Message controller handling messaging system
 * Allows users to send messages about rental properties
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
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
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            logger.error("Error sending message: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error sending message from user: {}", authentication.getName(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all messages for the authenticated user
     * Returns both sent and received messages
     */
    @GetMapping
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
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get messages by rental ID
     * Useful for viewing conversation about a specific rental
     */
    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByRental(@PathVariable Long rentalId,
                                                                   Authentication authentication) {
        logger.info("Request to get messages for rental {} by user: {}", 
                   rentalId, authentication.getName());
        
        try {
            String userEmail = authentication.getName();
            List<MessageResponse> messages = messageService.getMessagesByRental(rentalId, userEmail);
            
            logger.info("Successfully retrieved {} messages for rental {} and user: {}", 
                       messages.size(), rentalId, userEmail);
            return ResponseEntity.ok(messages);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Rental not found: {}", rentalId);
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("not authorized")) {
                logger.warn("User {} not authorized to view messages for rental {}", 
                           authentication.getName(), rentalId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error retrieving messages for rental: {}", rentalId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get message details by ID
     * Only accessible by sender or rental owner
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable Long id,
                                                        Authentication authentication) {
        logger.info("Request to get message {} by user: {}", id, authentication.getName());
        
        try {
            String userEmail = authentication.getName();
            MessageResponse message = messageService.getMessageById(id, userEmail);
            
            logger.info("Successfully retrieved message: {}", id);
            return ResponseEntity.ok(message);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Message not found: {}", id);
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("not authorized")) {
                logger.warn("User {} not authorized to view message {}", 
                           authentication.getName(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error retrieving message: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Mark message as read
     * Only the recipient can mark messages as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markMessageAsRead(@PathVariable Long id,
                                                               Authentication authentication) {
        logger.info("Request to mark message {} as read by user: {}", id, authentication.getName());
        
        try {
            String userEmail = authentication.getName();
            messageService.markAsRead(id, userEmail);
            
            logger.info("Message {} marked as read by user: {}", id, userEmail);
            
            Map<String, String> response = Map.of("message", "Message marked as read");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Message not found: {}", id);
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("not authorized")) {
                logger.warn("User {} not authorized to mark message {} as read", 
                           authentication.getName(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error marking message as read: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get unread message count for the authenticated user
     * Useful for notification badges
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(Authentication authentication) {
        logger.info("Request to get unread message count for user: {}", authentication.getName());
        
        try {
            String userEmail = authentication.getName();
            long unreadCount = messageService.getUnreadMessageCount(userEmail);
            
            logger.debug("User {} has {} unread messages", userEmail, unreadCount);
            
            Map<String, Long> response = Map.of("count", unreadCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting unread message count for user: {}", authentication.getName(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}