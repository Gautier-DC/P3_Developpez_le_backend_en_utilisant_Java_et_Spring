package com.openclassrooms.chatop.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

/**
 * DTO for message creation requests
 * Contains validation annotations for data integrity
 */
public class MessageRequest {

    @NotNull(message = "Rental ID is required")
    @Min(value = 1, message = "Rental ID must be greater than 0")
    @JsonProperty("rental_id")
    private Long rentalId;

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be greater than 0")
    @JsonProperty("user_id")
    private Long userId;

    @NotBlank(message = "Message content is required")
    @Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters")
    private String message;

    // Default constructor
    public MessageRequest() {
    }

    // Constructor with parameters
    public MessageRequest(Long rentalId, Long userId, String message) {
        this.rentalId = rentalId;
        this.userId = userId;
        this.message = message;
    }

    // Getters and Setters
    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "rentalId=" + rentalId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                '}';
    }
}
