package com.openclassrooms.chatop.dto.response;

import com.openclassrooms.chatop.entity.User;

/**
 * DTO for authentication response
 * Used for login and register endpoints responses
 * Contains JWT token and user information
 */
public class AuthResponse {
    
    private String token;
    private UserResponse user;
    
    // Default constructor
    public AuthResponse() {
    }
    
    // Constructor with parameters
    public AuthResponse(String token, UserResponse user) {
        this.token = token;
    }
    
    // Constructor with token
    public AuthResponse(String token, User user) {
        this.token = token;
    }
    
    // Static factory methods for different scenarios
    public static AuthResponse success(String token, User user) {
        return new AuthResponse(token, user);
    }
    
    public static AuthResponse success(String token, UserResponse userResponse) {
        return new AuthResponse(token, userResponse);
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='[PROTECTED]'" +
                '}';
    }
}