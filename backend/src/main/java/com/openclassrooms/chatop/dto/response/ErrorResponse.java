package com.openclassrooms.chatop.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Standard error response for API endpoints
 * Provides consistent error structure across all endpoints
 */
@Schema(description = "Error response returned when an API operation fails")
public class ErrorResponse {

    @Schema(description = "Human-readable error message", example = "Invalid email or password")
    private String message;

    @Schema(description = "Error code for client handling", example = "AUTH_001")
    private String code;

    @Schema(description = "Timestamp when the error occurred", example = "2025-01-15T10:30:00Z")
    private String timestamp;

    /**
     * Default constructor for JSON deserialization
     */
    public ErrorResponse() {
    }

    /**
     * Constructor with message and code
     * Automatically sets timestamp to current time
     */
    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
        this.timestamp = Instant.now().toString();
    }

    /**
     * Constructor with all fields
     */
    public ErrorResponse(String message, String code, String timestamp) {
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}