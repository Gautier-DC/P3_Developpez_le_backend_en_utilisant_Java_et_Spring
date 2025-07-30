package com.openclassrooms.chatop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openclassrooms.chatop.entity.Message;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for message response data
 * Used to send message information to clients
 * Uses snake_case naming for frontend compatibility
 */
public class MessageResponse {

    @Schema(description = "Unique message identifier", example = "1")
    private Long id;

    @Schema(description = "Content of the message", example = "Hello, I am interested in this rental.")
    private String message;

    @Schema(description = "Unique rental identifier", example = "1")
    @JsonProperty("rental_id")
    private Long rentalId;

    @Schema(description = "Name of the rental", example = "Cozy Apartment")
    @JsonProperty("rental_name")
    private String rentalName;

    @Schema(description = "Unique user identifier", example = "1")
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "Creation timestamp of the message", example = "2023-10-01T12:00:00Z")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp of the message", example = "2023-10-01T12:00:00Z")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public MessageResponse() {
    }

    // Constructor from Message entity
    public MessageResponse(Message messageEntity) {
        this.id = messageEntity.getId();
        this.message = messageEntity.getMessage();
        this.rentalId = messageEntity.getRental().getId();
        this.rentalName = messageEntity.getRental().getName();
        this.userId = messageEntity.getUser().getId();
        this.createdAt = messageEntity.getCreatedAt();
        this.updatedAt = messageEntity.getUpdatedAt();
    }

    // Constructor with parameters
    public MessageResponse(Long id, String message, Long rentalId, String rentalName,
            Long userId, String userName, String userEmail,
            Long recipientId, String recipientName, String recipientEmail, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.message = message;
        this.rentalId = rentalId;
        this.rentalName = rentalName;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Static factory method
    public static MessageResponse fromEntity(Message messageEntity) {
        return new MessageResponse(messageEntity);
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

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public String getRentalName() {
        return rentalName;
    }

    public void setRentalName(String rentalName) {
        this.rentalName = rentalName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setuserId(Long userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "MessageResponse{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", rentalId=" + rentalId +
                ", rentalName='" + rentalName + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}