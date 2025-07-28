package com.openclassrooms.chatop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openclassrooms.chatop.entity.Message;
import java.time.LocalDateTime;

/**
 * DTO for message response data
 * Used to send message information to clients
 * Uses snake_case naming for frontend compatibility
 */
public class MessageResponse {

    private Long id;
    private String message;

    @JsonProperty("rental_id")
    private Long rentalId;

    @JsonProperty("rental_name")
    private String rentalName;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("recipient_name")
    private String recipientName;

    @JsonProperty("recipient_email")
    private String recipientEmail;

    @JsonProperty("is_read")
    private Boolean isRead;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

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
        this.userName = messageEntity.getUser().getName();
        this.userEmail = messageEntity.getUser().getEmail();
        this.recipientId = messageEntity.getRental().getOwner().getId();
        this.recipientName = messageEntity.getRental().getOwner().getName();
        this.recipientEmail = messageEntity.getRental().getOwner().getEmail();
        this.createdAt = messageEntity.getCreatedAt();
        this.updatedAt = messageEntity.getUpdatedAt();
    }

    // Constructor with parameters
    public MessageResponse(Long id, String message, Long rentalId, String rentalName,
                          Long userId, String userName, String userEmail,
                          Long recipientId, String recipientName, String recipientEmail, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.message = message;
        this.rentalId = rentalId;
        this.rentalName = rentalName;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
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

    public String getUserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setuserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
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
                ", userName='" + userName + '\'' +
                ", recipientId=" + recipientId +
                ", recipientName='" + recipientName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}