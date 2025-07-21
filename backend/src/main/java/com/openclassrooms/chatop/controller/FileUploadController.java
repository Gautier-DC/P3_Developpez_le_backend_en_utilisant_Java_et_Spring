package com.openclassrooms.chatop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        logger.info("Image upload request received: {}", file.getOriginalFilename());

        try {
            // Validate file
            if (file.isEmpty()) {
                logger.warn("Empty file received");
                return ResponseEntity.badRequest().build();
            }

            // Validate file type
            if (!isImageFile(file)) {
                logger.warn("Invalid file type: {}", file.getContentType());
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only image files are allowed (JPEG, PNG, GIF)");
                return ResponseEntity.badRequest().body(error);
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

        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload image");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Validate if file is an image
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif") ||
            contentType.equals("image/webp")
        );
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