package com.openclassrooms.chatop.service;

import com.openclassrooms.chatop.dto.request.MessageRequest;
import com.openclassrooms.chatop.dto.response.MessageResponse;
import com.openclassrooms.chatop.entity.Message;
import com.openclassrooms.chatop.entity.Rental;
import com.openclassrooms.chatop.entity.User;
import com.openclassrooms.chatop.repository.MessageRepository;
import com.openclassrooms.chatop.repository.RentalRepository;
import com.openclassrooms.chatop.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling message business logic
 * Manages messaging system between users and rental owners
 */
@Service
@Transactional
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public MessageService(MessageRepository messageRepository, 
                         UserRepository userRepository,
                         RentalRepository rentalRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    /**
     * Send a message about a rental
     * The recipient is automatically the rental owner
     */
    public void sendMessage(MessageRequest request, String userEmail) {
        logger.info("Sending message from {} about rental {}", userEmail, request.getRentalId());
        
        try {
            // Validate request
            validateMessageRequest(request);
            
            // Find user - can use email from JWT or user_id from request
            User user;
            if (request.getUserId() != null) {
                // Use user_id if provided
                user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> {
                            logger.error("user not found with ID: {}", request.getUserId());
                            return new RuntimeException("user not found with ID: " + request.getUserId());
                        });
                
                // Verify that the JWT email matches the user_id (security check)
                if (!user.getEmail().equals(userEmail)) {
                    logger.warn("JWT email {} doesn't match user_id {} email {}", 
                               userEmail, request.getUserId(), user.getEmail());
                    throw new RuntimeException("JWT token doesn't match provided user_id");
                }
            } else {
                // Fallback to email from JWT
                user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> {
                            logger.error("user not found: {}", userEmail);
                            return new RuntimeException("user not found: " + userEmail);
                        });
            }
            
            // Find rental
            Rental rental = rentalRepository.findById(request.getRentalId())
                    .orElseThrow(() -> {
                        logger.error("Rental not found: {}", request.getRentalId());
                        return new RuntimeException("Rental not found with ID: " + request.getRentalId());
                    });
            
            // Prevent owner from messaging themselves
            if (rental.getOwner().getId().equals(user.getId())) {
                logger.warn("User {} tried to send message to their own rental {}", 
                           user.getId(), request.getRentalId());
                throw new RuntimeException("Cannot send message to your own rental");
            }
            
            // Create message
            Message message = new Message();
            message.setMessage(request.getMessage());
            message.setUser(user);
            message.setRental(rental);
            message.setRead(false); // New messages are unread
            message.setCreatedAt(LocalDateTime.now());
            message.setUpdatedAt(LocalDateTime.now());
            
            // Save message
            messageRepository.save(message);
            
            logger.info("Message sent successfully from {} (ID: {}) to {} about rental {}", 
                       user.getEmail(), user.getId(), rental.getOwner().getEmail(), request.getRentalId());
                       
        } catch (RuntimeException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error sending message from {}", userEmail, e);
            throw new RuntimeException("Failed to send message");
        }
    }

    /**
     * Get all messages for a user (sent and received)
     * Returns messages where user is either user or rental owner
     */
    public List<MessageResponse> getUserMessages(String userEmail) {
        logger.info("Retrieving messages for user: {}", userEmail);
        
        try {
            // Find user
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", userEmail);
                        return new RuntimeException("User not found: " + userEmail);
                    });
            
            // Get messages where user is user or rental owner
            List<Message> messages = messageRepository.findByUserInvolvement(user);
            
            logger.info("Found {} messages for user: {}", messages.size(), userEmail);
            
            return messages.stream()
                    .map(MessageResponse::new)
                    .collect(Collectors.toList());
                    
        } catch (RuntimeException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error retrieving messages for user: {}", userEmail, e);
            throw new RuntimeException("Failed to retrieve messages");
        }
    }

    /**
     * Get messages by rental ID for authorized users
     * Only rental owner and message users can see rental messages
     */
    public List<MessageResponse> getMessagesByRental(Long rentalId, String userEmail) {
        logger.info("Retrieving messages for rental {} by user: {}", rentalId, userEmail);
        
        try {
            // Find user
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", userEmail);
                        return new RuntimeException("User not found: " + userEmail);
                    });
            
            // Find rental
            Rental rental = rentalRepository.findById(rentalId)
                    .orElseThrow(() -> {
                        logger.error("Rental not found: {}", rentalId);
                        return new RuntimeException("Rental not found with ID: " + rentalId);
                    });
            
            // Check authorization - user must be rental owner or have sent messages about this rental
            boolean isRentalOwner = rental.getOwner().getEmail().equals(userEmail);
            boolean hasSentMessages = messageRepository.existsByUserAndRental(user, rental);
            
            if (!isRentalOwner && !hasSentMessages) {
                logger.warn("User {} not authorized to view messages for rental {}", userEmail, rentalId);
                throw new RuntimeException("User not authorized to view messages for this rental");
            }
            
            // Get messages for this rental
            List<Message> messages = messageRepository.findByRental(rental);
            
            logger.info("Found {} messages for rental {} and user: {}", 
                       messages.size(), rentalId, userEmail);
            
            return messages.stream()
                    .map(MessageResponse::new)
                    .collect(Collectors.toList());
                    
        } catch (RuntimeException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error retrieving messages for rental: {}", rentalId, e);
            throw new RuntimeException("Failed to retrieve rental messages");
        }
    }

    /**
     * Get message by ID for authorized users
     * Only user and rental owner can view the message
     */
    public MessageResponse getMessageById(Long messageId, String userEmail) {
        logger.info("Retrieving message {} for user: {}", messageId, userEmail);
        
        try {
            // Find user
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", userEmail);
                        return new RuntimeException("User not found: " + userEmail);
                    });
            
            // Find message
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> {
                        logger.error("Message not found: {}", messageId);
                        return new RuntimeException("Message not found with ID: " + messageId);
                    });
            
            // Check authorization
            boolean isuser = message.getUser().getEmail().equals(userEmail);
            boolean isRentalOwner = message.getRental().getOwner().getEmail().equals(userEmail);
            
            if (!isuser && !isRentalOwner) {
                logger.warn("User {} not authorized to view message {}", userEmail, messageId);
                throw new RuntimeException("User not authorized to view this message");
            }
            
            logger.info("Successfully retrieved message: {}", messageId);
            return new MessageResponse(message);
            
        } catch (RuntimeException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error retrieving message: {}", messageId, e);
            throw new RuntimeException("Failed to retrieve message");
        }
    }

    /**
     * Mark message as read
     * Only the rental owner (recipient) can mark messages as read
     */
    public void markAsRead(Long messageId, String userEmail) {
        logger.info("Marking message {} as read by user: {}", messageId, userEmail);
        
        try {
            // Find message
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> {
                        logger.error("Message not found: {}", messageId);
                        return new RuntimeException("Message not found with ID: " + messageId);
                    });
            
            // Check authorization - only rental owner can mark as read
            if (!message.getRental().getOwner().getEmail().equals(userEmail)) {
                logger.warn("User {} not authorized to mark message {} as read", userEmail, messageId);
                throw new RuntimeException("User not authorized to mark this message as read");
            }
            
            // Mark as read if not already
            if (!message.isRead()) {
                message.setRead(true);
                message.setUpdatedAt(LocalDateTime.now());
                messageRepository.save(message);
                
                logger.info("Message {} marked as read by user: {}", messageId, userEmail);
            } else {
                logger.debug("Message {} was already marked as read", messageId);
            }
            
        } catch (RuntimeException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error marking message as read: {}", messageId, e);
            throw new RuntimeException("Failed to mark message as read");
        }
    }

    /**
     * Get count of unread messages for a user
     * Only counts messages where user is the rental owner (recipient)
     */
    public long getUnreadMessageCount(String userEmail) {
        logger.debug("Getting unread message count for user: {}", userEmail);
        
        try {
            // Find user
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", userEmail);
                        return new RuntimeException("User not found: " + userEmail);
                    });
            
            // Count unread messages where user is rental owner
            long count = messageRepository.countUnreadMessagesForOwner(user);
            
            logger.debug("User {} has {} unread messages", userEmail, count);
            return count;
            
        } catch (RuntimeException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error getting unread message count for user: {}", userEmail, e);
            return 0; // Return 0 on error to avoid breaking UI
        }
    }

    /**
     * Get message statistics for a user
     * Useful for dashboard or analytics
     */
    public MessageStatistics getUserMessageStatistics(String userEmail) {
        logger.info("Getting message statistics for user: {}", userEmail);
        
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
            
            long totalSent = messageRepository.countByUser(user);
            long totalReceived = messageRepository.countByRentalOwner(user);
            long unreadReceived = messageRepository.countUnreadMessagesForOwner(user);
            
            return new MessageStatistics(totalSent, totalReceived, unreadReceived);
            
        } catch (Exception e) {
            logger.error("Error calculating message statistics for user: {}", userEmail, e);
            throw new RuntimeException("Failed to calculate message statistics");
        }
    }

    /**
     * Validate message request data
     * Centralized validation logic
     */
    private void validateMessageRequest(MessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Message request cannot be null");
        }
        
        if (request.getRentalId() == null || request.getRentalId() <= 0) {
            throw new IllegalArgumentException("Valid rental ID is required");
        }
        
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message content is required");
        }
        
        if (request.getMessage().length() < 10) {
            throw new IllegalArgumentException("Message must be at least 10 characters long");
        }
        
        if (request.getMessage().length() > 2000) {
            throw new IllegalArgumentException("Message must be less than 2000 characters");
        }
        
        // user_id is optional - if provided, will be validated against JWT
        if (request.getUserId() != null && request.getUserId() <= 0) {
            throw new IllegalArgumentException("Valid user ID is required if provided");
        }
        
        logger.debug("Message request validation passed for rental: {}", request.getRentalId());
    }

    /**
     * Inner class for message statistics
     */
    public static class MessageStatistics {
        private final long totalSent;
        private final long totalReceived;
        private final long unreadReceived;
        
        public MessageStatistics(long totalSent, long totalReceived, long unreadReceived) {
            this.totalSent = totalSent;
            this.totalReceived = totalReceived;
            this.unreadReceived = unreadReceived;
        }
        
        // Getters
        public long getTotalSent() { return totalSent; }
        public long getTotalReceived() { return totalReceived; }
        public long getUnreadReceived() { return unreadReceived; }
    }
}