package com.openclassrooms.chatop.exceptions;

import com.openclassrooms.chatop.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * Global exception handler for all REST controllers
 * Provides consistent error responses with automatic error code generation
 */
@RestControllerAdvice
public class SimpleExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SimpleExceptionHandler.class);

    /**
     * Handle ResponseStatusException with automatic error code generation
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e) {
        HttpStatus status = (HttpStatus) e.getStatusCode();
        String message = e.getReason();

        // ✅ Generate appropriate error code automatically
        String errorCode = generateErrorCode(message, status);

        logger.warn("ResponseStatusException: {} - Code: {}", message, errorCode);

        ErrorResponse error = new ErrorResponse(message, errorCode);
        return ResponseEntity.status(status).body(error);
    }

    /**
     * Handle validation errors (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        String fullMessage = "Validation failed: " + message;
        ErrorResponse error = new ErrorResponse(fullMessage, "VALIDATION_400");

        logger.warn("Validation error: {}", fullMessage);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle generic exceptions as fallback
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);

        ErrorResponse error = new ErrorResponse("Internal server error", "SERVER_500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Generate appropriate error code based on message content and HTTP status
     */
    private String generateErrorCode(String message, HttpStatus status) {
        if (message == null) {
            return "SERVER_" + status.value();
        }

        String lowerMessage = message.toLowerCase();

        // Authentication & Authorization errors
        if (lowerMessage.contains("invalid email") ||
                lowerMessage.contains("invalid password") ||
                lowerMessage.contains("credentials") ||
                lowerMessage.contains("login")) {
            return "AUTH_" + status.value();
        }

        if (lowerMessage.contains("email already") ||
                lowerMessage.contains("already registered")) {
            return "AUTH_" + status.value();
        }

        // Rental-related errors
        if (lowerMessage.contains("rental")) {
            return "RENTAL_" + status.value();
        }

        // User-related errors
        if (lowerMessage.contains("user")) {
            return "USER_" + status.value();
        }

        // Message-related errors
        if (lowerMessage.contains("message")) {
            return "MESSAGE_" + status.value();
        }

        // File upload errors
        if (lowerMessage.contains("file") ||
                lowerMessage.contains("upload") ||
                lowerMessage.contains("image") ||
                lowerMessage.contains("picture")) {
            return "UPLOAD_" + status.value();
        }

        // Validation errors
        if (lowerMessage.contains("validation") ||
                lowerMessage.contains("invalid") ||
                lowerMessage.contains("required") ||
                lowerMessage.contains("empty")) {
            return "VALIDATION_" + status.value();
        }

        // Authorization errors
        if (lowerMessage.contains("not authorized") ||
                lowerMessage.contains("forbidden") ||
                lowerMessage.contains("permission")) {
            return "ACCESS_" + status.value();
        }

        // Not found errors
        if (lowerMessage.contains("not found")) {
            return "RESOURCE_" + status.value();
        }

        // Default fallback based on HTTP status
        switch (status.value()) {
            case 400:
                return "REQUEST_400";
            case 401:
                return "AUTH_401";
            case 403:
                return "ACCESS_403";
            case 404:
                return "RESOURCE_404";
            case 413:
                return "UPLOAD_413";
            case 500:
                return "SERVER_500";
            default:
                return "ERROR_" + status.value();
        }
    }
}