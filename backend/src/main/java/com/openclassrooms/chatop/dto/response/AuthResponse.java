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
        this.user = user;
    }
    
    // Constructor with token and User entity
    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = new UserResponse(user);
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
    
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='[PROTECTED]'" +
                ", user=" + user +
                '}';
    }
}