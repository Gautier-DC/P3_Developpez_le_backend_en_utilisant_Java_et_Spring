package com.openclassrooms.chatop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.openclassrooms.chatop.dto.response.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for handling file uploads
 * Manages image uploads for rental properties
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "File Upload", description = "File upload management endpoints")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${file.upload-dir:./uploads/images}")
    private String uploadDir;

    @Value("${server.port:3001}")
    private String serverPort;

    /**
     * Upload an image file
     * Returns the URL of the uploaded image
     */
    @PostMapping("/image")
    @Operation(summary = "Upload an image file", description = "Upload an image file for rental properties. Supports JPEG, PNG, GIF, and WebP formats.", tags = {
            "File Upload" }, security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Upload Success", value = """
                    {
                        "url": "http://localhost:3001/images/abc123-def456.jpg",
                        "filename": "abc123-def456.jpg",
                        "originalName": "my-apartment.jpg"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid file or file type not supported", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Invalid File Type", value = """
                    {
                        "message": "Only image files are allowed (JPEG, PNG, GIF, WebP)",
                        "code": "REQUEST_400",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = """
                    {
                        "message": "Unauthorized - Invalid token",
                        "code": "AUTH_401",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "413", description = "File too large", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "File Too Large", value = """
                    {
                        "message": "File size exceeds maximum allowed limit",
                        "code": "UPLOAD_413",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Server Error", value = """
                    {
                        "message": "Failed to upload image",
                        "code": "SERVER_500",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """)))
    })
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        logger.info("Image upload request received: {}", file.getOriginalFilename());

        try {
            // Validate file
            if (file.isEmpty()) {
                logger.warn("Empty file received");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty");
            }

            // Validate file type
            if (!isImageFile(file)) {
                logger.warn("Invalid file type: {}", file.getContentType());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Only image files are allowed (JPEG, PNG, GIF, WebP)");
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", uploadPath);
            }

            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Generate URL
            String imageUrl = "http://localhost:" + serverPort + "/images/" + uniqueFilename;

            logger.info("Image uploaded successfully: {} -> {}", originalFilename, uniqueFilename);

            // Return response with image URL
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("filename", uniqueFilename);
            response.put("originalName", originalFilename);

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            throw e; // Re-throw ResponseStatusException as-is
        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image");
        } catch (Exception e) {
            logger.error("Unexpected error during image upload: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Validate if file is an image
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp"));
    }

    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return ".jpg"; // Default extension
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}