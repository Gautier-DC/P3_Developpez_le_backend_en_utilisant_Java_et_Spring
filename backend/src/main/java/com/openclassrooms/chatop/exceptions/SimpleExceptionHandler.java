package com.openclassrooms.chatop.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.openclassrooms.chatop.dto.response.ErrorResponse;

@RestControllerAdvice
public class SimpleExceptionHandler {
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e) {
        ErrorResponse error = new ErrorResponse(e.getReason(), "ERROR_001");
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }
}