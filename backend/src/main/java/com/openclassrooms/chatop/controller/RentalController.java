package com.openclassrooms.chatop.controller;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.openclassrooms.chatop.dto.request.RentalRequest;
import com.openclassrooms.chatop.dto.response.ErrorResponse;
import com.openclassrooms.chatop.dto.response.RentalResponse;
import com.openclassrooms.chatop.entity.Rental;
import com.openclassrooms.chatop.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Rental controller handling CRUD operations for rental properties
 * All endpoints are protected and require authentication (JWT Token)
 */

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Rentals", description = "Rental property management endpoints")
public class RentalController {

    private static final Logger logger = LoggerFactory.getLogger(RentalController.class);

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * Get all rentals
     * Public for browsing available rentals
     */
    @GetMapping
    @Operation(summary = "Get all rentals", description = "Retrieve all available rental properties.", tags = {
            "Rentals" }, security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rentals retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponse.class), examples = @ExampleObject(name = "Rentals List", value = """
                    [
                        {
                            "id": 1,
                            "name": "Beautiful Apartment",
                            "surface": 75.5,
                            "price": 1200.00,
                            "description": "A lovely apartment in the city center",
                            "picture": "http://localhost:3001/images/apartment1.jpg",
                            "owner_id": 2,
                            "created_at": "2025-01-15T10:30:00",
                            "updated_at": "2025-01-15T10:30:00"
                        }
                    ]
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = """
                    {
                        "message": "Unauthorized - Invalid token",
                        "code": "ERROR_001",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        logger.info("Request to get all rentals");

        try {
            List<RentalResponse> rentals = rentalService.getAllRentals();
            logger.info("Successfully retrieved {} rentals", rentals.size());
            return ResponseEntity.ok(rentals);

        } catch (Exception e) {
            logger.error("Error retrieving all rentals", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Get rental by ID
     * Public for viewing rental details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get rental by ID", description = "Retrieve a specific rental property by its ID.", tags = {
            "Rentals" }, security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponse.class), examples = @ExampleObject(name = "Rental Details", value = """
                    {
                        "id": 1,
                        "name": "Beautiful Apartment",
                        "surface": 75.5,
                        "price": 1200.00,
                        "description": "A lovely apartment in the city center",
                        "picture": "http://localhost:3001/images/apartment1.jpg",
                        "owner_id": 2,
                        "created_at": "2025-01-15T10:30:00",
                        "updated_at": "2025-01-15T10:30:00"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rental not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Rental Not Found", value = """
                    {
                        "message": "Rental not found",
                        "code": "ERROR_001",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """)))
    })
    public ResponseEntity<RentalResponse> getRentalById(@PathVariable Long id) {
        logger.info("Request to get rental with ID: {}", id);

        try {
            RentalResponse rental = rentalService.getRentalById(id);
            logger.info("Successfully retrieved rental: {}", rental.getName());
            return ResponseEntity.ok(rental);

        } catch (RuntimeException e) {
            logger.warn("Rental not found with ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found");
        } catch (Exception e) {
            logger.error("Error retrieving rental with ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Create a new rental property
     * Requires authentication - rental will be associated with authenticated user
     * Accepts FormData with file upload for picture
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new rental", description = "Create a new rental property with picture upload. Requires authentication.", tags = {
            "Rentals" }, security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponse.class), examples = @ExampleObject(name = "Created Rental", value = """
                    {
                        "id": 1,
                        "name": "Beautiful Apartment",
                        "surface": 75.5,
                        "price": 1200.00,
                        "description": "A lovely apartment in the city center",
                        "picture": "http://localhost:3001/images/apartment1.jpg",
                        "owner_id": 2,
                        "created_at": "2025-01-15T10:30:00",
                        "updated_at": "2025-01-15T10:30:00"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid rental data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Invalid Data", value = """
                    {
                        "message": "Invalid rental data",
                        "code": "ERROR_001",
                        "timestamp": "2025-01-15T10:30:00Z"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RentalResponse> createRental(@RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("picture") MultipartFile pictureFile,
            Authentication authentication) {
        logger.info("Request to create new rental by user: {}", authentication.getName());

        try {
            String userEmail = authentication.getName();

            // Create rental request from form parameters
            RentalRequest request = new RentalRequest();
            request.setName(name);
            request.setSurface(surface);
            request.setPrice(price);
            request.setDescription(description);

            // Handle picture upload
            String pictureUrl = handlePictureUpload(pictureFile);
            request.setPicture(pictureUrl);

            RentalResponse rental = rentalService.createRental(request, userEmail);
            logger.info("Successfully created rental with ID: {}", rental.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(rental);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid rental data: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rental data");
        } catch (Exception e) {
            logger.error("Error creating rental for user: {}", authentication.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    
    /**
     * Update an existing rental property
     * Only the owner of the rental can update it
     * Accepts FormData with optional file upload for picture
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Update an existing rental",
        description = "Update a rental property. Only the owner can update it.",
        tags = {"Rentals"},
        security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rental updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RentalResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid rental data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Not the owner of this rental",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Forbidden",
                    value = """
                        {
                            "message": "Not authorized to update this rental",
                            "code": "ERROR_001",
                            "timestamp": "2025-01-15T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Rental not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<RentalResponse> updateRental(@PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile pictureFile,
            Authentication authentication) {
        logger.info("Request to update rental {} by user: {}", id, authentication.getName());

        try {
            String userEmail = authentication.getName();

            // Get existing rental to preserve picture if no new one uploaded
            Rental existingRental = rentalService.getRentalEntityById(id);
            if (existingRental == null) {
                logger.warn("Rental not found with ID: {}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found");
            }

            // Check if user is the owner
            if (!existingRental.getOwner().getEmail().equals(userEmail)) {
                logger.warn("User {} is not authorized to update rental {}", userEmail, id);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this rental");
            }

            // Create rental request from form parameters
            RentalRequest request = new RentalRequest();
            request.setName(name);
            request.setSurface(surface);
            request.setPrice(price);
            request.setDescription(description);

            // Handle picture upload (optional for update)
            if (pictureFile != null && !pictureFile.isEmpty()) {
                logger.info("New picture uploaded for rental {}: {}", id, pictureFile.getOriginalFilename());
                String pictureUrl = handlePictureUpload(pictureFile);
                request.setPicture(pictureUrl);
            } else {
                // Keep existing picture if no new one provided
                logger.info("No new picture provided, keeping existing picture for rental {}", id);
                request.setPicture(existingRental.getPicture());
            }

            RentalResponse rental = rentalService.updateRental(id, request, userEmail);
            logger.info("Successfully updated rental with ID: {}", id);
            return ResponseEntity.ok(rental);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Rental not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("not authorized")) {
                logger.warn("User {} not authorized to update rental {}", authentication.getName(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                logger.error("Error updating rental: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("Error updating rental with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all rentals owned by the authenticated user
     * Private endpoint for rental management
     */
    @GetMapping("/user")
    @Operation(
        summary = "Get user's rentals",
        description = "Retrieve all rental properties owned by the authenticated user.",
        tags = {"Rentals"},
        security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User rentals retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RentalResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<List<RentalResponse>> getUserRentals(Authentication authentication) {
        logger.info("Request to get rentals for user: {}", authentication.getName());

        try {
            String userEmail = authentication.getName();
            List<RentalResponse> rentals = rentalService.getRentalsByOwner(userEmail);
            logger.info("Successfully retrieved {} rentals for user: {}", rentals.size(), userEmail);
            return ResponseEntity.ok(rentals);

        } catch (Exception e) {
            logger.error("Error retrieving rentals for user: {}", authentication.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Handle picture upload and return URL
     */
    private String handlePictureUpload(MultipartFile pictureFile) {
        if (pictureFile == null || pictureFile.isEmpty()) {
            throw new IllegalArgumentException("Picture is required");
        }

        // Validate file type
        String contentType = pictureFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        try {
            // Generate unique filename
            String originalFilename = pictureFile.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;

            // Create upload directory if it doesn't exist
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("./uploads/images");
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }

            // Save file
            java.nio.file.Path filePath = uploadPath.resolve(uniqueFilename);
            java.nio.file.Files.copy(pictureFile.getInputStream(), filePath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Return URL
            return "http://localhost:3001/images/" + uniqueFilename;

        } catch (Exception e) {
            logger.error("Error uploading picture: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload picture");
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

}
