package com.openclassrooms.chatop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Message entity representing messages sent about rental properties
 * Links users (senders) to rentals with message message
 */
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Message() {
    }

    public Message(String message, User user, Rental rental) {
        this.message = message;
        this.user = user;
        this.rental = rental;
    }

    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    // Business methods

    /**
     * Check if the message is from a specific user
     */
    public boolean isFromUser(User user) {
        return this.user != null && this.user.equals(user);
    }

    /**
     * Check if the message is for a specific user (rental owner)
     */
    public boolean isForUser(User user) {
        return rental != null && rental.getOwner() != null && rental.getOwner().equals(user);
    }

    /**
     * Get the recipient of the message (rental owner)
     */
    public User getRecipient() {
        return rental != null ? rental.getOwner() : null;
    }

    /**
     * Check if a user is involved in this message (sender or recipient)
     */
    public boolean involvesUser(User user) {
        return isFromUser(user) || isForUser(user);
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Message))
            return false;
        Message message = (Message) o;
        return id != null && id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + (message != null ? message.substring(0, Math.min(50, message.length())) + "..." : null)
                + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                ", rentalId=" + (rental != null ? rental.getId() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}