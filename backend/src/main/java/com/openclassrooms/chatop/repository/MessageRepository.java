package com.openclassrooms.chatop.repository;

import com.openclassrooms.chatop.entity.Message;
import com.openclassrooms.chatop.entity.Rental;
import com.openclassrooms.chatop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Message entity
 * Contains only essential methods used by MessageService
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all messages sent by a specific user
     */
    List<Message> findByUser(User user);

    /**
     * Find all messages about a specific rental
     */
    @Query("SELECT m FROM Message m WHERE m.rental = :rental ORDER BY m.createdAt DESC")
    List<Message> findByRental(@Param("rental") Rental rental);

    /**
     * Find all messages where a user is involved (user or rental owner)
     */
    @Query("SELECT m FROM Message m WHERE m.user = :user OR m.rental.owner = :user ORDER BY m.createdAt DESC")
    List<Message> findByUserInvolvement(@Param("user") User user);

    /**
     * Check if a user has sent any messages about a specific rental
     */
    boolean existsByUserAndRental(User user, Rental rental);

    /**
     * Count total messages sent by a user
     */
    long countByUser(User user);

    /**
     * Count total messages received by a user (as rental owner)
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.rental.owner = :owner")
    long countByRentalOwner(@Param("owner") User owner);

    /**
     * Count unread messages for a rental owner
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.rental.owner = :owner AND m.isRead = false")
    long countUnreadMessagesForOwner(@Param("owner") User owner);
}