package com.openclassrooms.chatop.repository;

import com.openclassrooms.chatop.entity.Message;
import com.openclassrooms.chatop.entity.Rental;
import com.openclassrooms.chatop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Message entity
 * Handles data access for messages between users regarding rentals
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * Finds all messages related to a specific rental
     * Used to display message history for a rental listing
     * 
     * @param rental The rental property
     * @return List of messages for this rental, sorted by creation date
     */
    List<Message> findByRentalOrderByCreatedAtAsc(Rental rental);
    
    /**
     * Finds all messages related to a rental by its ID
     * More convenient alternative when only having the rental ID
     * 
     * @param rentalId The rental ID
     * @return List of messages for this rental, sorted by creation date
     */
    List<Message> findByRentalIdOrderByCreatedAtAsc(Long rentalId);
    
    /**
     * Finds all messages sent by a specific user
     * Used for user's sent messages history
     * 
     * @param user The user who sent the messages
     * @return List of messages sent by this user, sorted by creation date (newest first)
     */
    List<Message> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Finds all messages sent by a user by their ID
     * More convenient alternative when only having the user ID
     * 
     * @param userId The user ID
     * @return List of messages sent by this user, sorted by creation date (newest first)
     */
    List<Message> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Finds messages for a specific rental and user combination
     * Used to display conversation between a user and rental owner
     * 
     * @param rental The rental property
     * @param user The user who sent messages
     * @return List of messages for this rental from this user, sorted by date
     */
    List<Message> findByRentalAndUserOrderByCreatedAtAsc(Rental rental, User user);
    
    /**
     * Finds messages received by a rental owner
     * Gets all messages for rentals owned by the specified user
     * 
     * @param owner The rental owner
     * @return List of messages received for all owner's rentals
     */
    @Query("SELECT m FROM Message m WHERE m.rental.owner = :owner ORDER BY m.createdAt DESC")
    List<Message> findMessagesReceivedByOwner(@Param("owner") User owner);
    
    /**
     * Finds recent messages for a rental (last 30 days)
     * Used to display recent activity on a rental listing
     * 
     * @param rental The rental property
     * @param since Date threshold (typically 30 days ago)
     * @return List of recent messages for this rental
     */
    List<Message> findByRentalAndCreatedAtAfterOrderByCreatedAtDesc(Rental rental, LocalDateTime since);
    
    /**
     * Searches messages by content (case-insensitive)
     * Used for message search functionality
     * 
     * @param keyword Keyword to search for in message content
     * @return List of messages containing the keyword
     */
    @Query("SELECT m FROM Message m WHERE LOWER(m.message) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY m.createdAt DESC")
    List<Message> findByMessageContainingIgnoreCase(@Param("keyword") String keyword);
    
    /**
     * Counts messages for a specific rental
     * Used to display message count on rental listings
     * 
     * @param rental The rental property
     * @return Number of messages for this rental
     */
    long countByRental(Rental rental);
    
    /**
     * Counts messages sent by a user
     * Used for user statistics
     * 
     * @param user The user
     * @return Number of messages sent by this user
     */
    long countByUser(User user);
    
    /**
     * Finds the most recent message for each rental owned by a user
     * Used to display latest inquiries for rental owner dashboard
     * 
     * @param owner The rental owner
     * @return List of latest messages per rental
     */
    @Query("SELECT m FROM Message m WHERE m.rental.owner = :owner " +
           "AND m.createdAt = (SELECT MAX(m2.createdAt) FROM Message m2 WHERE m2.rental = m.rental) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findLatestMessagePerRentalForOwner(@Param("owner") User owner);
    
    /**
     * Checks if a user has already sent a message for a specific rental
     * Used to prevent spam or track user engagement
     * 
     * @param user The user
     * @param rental The rental property
     * @return true if user has already messaged about this rental
     */
    boolean existsByUserAndRental(User user, Rental rental);
    
    /**
     * Finds messages between two users for a specific rental
     * Used to display conversation thread
     * 
     * @param rental The rental property
     * @param user1 First user (could be sender or receiver)
     * @param user2 Second user (could be sender or receiver)
     * @return List of messages in the conversation, sorted by date
     */
    @Query("SELECT m FROM Message m WHERE m.rental = :rental " +
           "AND (m.user = :user1 OR m.user = :user2) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversationBetweenUsers(@Param("rental") Rental rental,
                                              @Param("user1") User user1,
                                              @Param("user2") User user2);
}
