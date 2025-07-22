package com.openclassrooms.chatop.service;

import com.openclassrooms.chatop.dto.request.RentalRequest;
import com.openclassrooms.chatop.dto.response.RentalResponse;
import com.openclassrooms.chatop.entity.Rental;
import com.openclassrooms.chatop.entity.User;
import com.openclassrooms.chatop.repository.RentalRepository;
import com.openclassrooms.chatop.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling rental business logic
 * Manages CRUD operations, permissions, and data validation for rental properties
 */
@Service
@Transactional
public class RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all available rental properties
     * Returns all rentals in the system for browsing
     */
    public List<RentalResponse> getAllRentals() {
        logger.info("Retrieving all rentals from database");
        
        try {
            List<Rental> rentals = rentalRepository.findAll();
            logger.info("Found {} rentals in database", rentals.size());
            
            return rentals.stream()
                    .map(RentalResponse::new)  // Convert Entity to DTO
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error retrieving all rentals", e);
            throw new RuntimeException("Failed to retrieve rentals");
        }
    }

    /**
     * Get a specific rental by ID
     * Returns detailed information about a single rental
     */
    public RentalResponse getRentalById(Long id) {
        logger.info("Retrieving rental with ID: {}", id);
        
        if (id == null || id <= 0) {
            logger.warn("Invalid rental ID provided: {}", id);
            throw new IllegalArgumentException("Invalid rental ID");
        }
        
        try {
            Rental rental = rentalRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Rental not found with ID: {}", id);
                        return new RuntimeException("Rental not found with ID: " + id);
                    });
            
            logger.info("Successfully retrieved rental: {} (ID: {})", rental.getName(), id);
            return new RentalResponse(rental);
            
        } catch (RuntimeException e) {
            throw e;  // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error retrieving rental with ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve rental");
        }
    }

    /**
     * Create a new rental property
     * Associates the rental with the authenticated user as owner
     */
    public RentalResponse createRental(RentalRequest request, String userEmail) {
        logger.info("Creating new rental: {} for user: {}", request.getName(), userEmail);
        
        // Validate input
        validateRentalRequest(request);
        
        try {
            // Find the user who will own this rental
            User owner = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", userEmail);
                        return new RuntimeException("User not found: " + userEmail);
                    });
            
            // Create new rental entity
            Rental rental = new Rental();
            rental.setName(request.getName());
            rental.setSurface(request.getSurface());
            rental.setPrice(request.getPrice());
            rental.setDescription(request.getDescription());
            rental.setPicture(request.getPicture());
            rental.setOwner(owner);
            rental.setCreatedAt(LocalDateTime.now());
            rental.setUpdatedAt(LocalDateTime.now());
            
            // Save to database
            Rental savedRental = rentalRepository.save(rental);
            logger.info("Successfully created rental with ID: {} for user: {}", savedRental.getId(), userEmail);
            
            return new RentalResponse(savedRental);
            
        } catch (RuntimeException e) {
            throw e;  // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error creating rental for user: {}", userEmail, e);
            throw new RuntimeException("Failed to create rental");
        }
    }

    /**
     * Update an existing rental property
     * Only the owner of the rental can perform updates
     */
    public RentalResponse updateRental(Long id, RentalRequest request, String userEmail) {
        logger.info("Updating rental {} for user: {}", id, userEmail);
        
        // Validate input
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid rental ID");
        }
        validateRentalRequest(request);
        
        try {
            // Find the rental to update
            Rental rental = rentalRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Rental not found with ID: {}", id);
                        return new RuntimeException("Rental not found with ID: " + id);
                    });
            
            // Check if user is the owner
            if (!rental.getOwner().getEmail().equals(userEmail)) {
                logger.warn("User {} is not authorized to update rental {}", userEmail, id);
                throw new RuntimeException("User not authorized to update this rental");
            }
            
            // Update rental properties
            rental.setName(request.getName());
            rental.setSurface(request.getSurface());
            rental.setPrice(request.getPrice());
            rental.setDescription(request.getDescription());
            rental.setPicture(request.getPicture());
            rental.setUpdatedAt(LocalDateTime.now());
            
            // Save updated rental
            Rental updatedRental = rentalRepository.save(rental);
            logger.info("Successfully updated rental with ID: {}", id);
            
            return new RentalResponse(updatedRental);
            
        } catch (RuntimeException e) {
            throw e;  // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error updating rental with ID: {}", id, e);
            throw new RuntimeException("Failed to update rental");
        }
    }

    /**
     * Get all rentals owned by a specific user
     * Used for rental management dashboard
     */
    public List<RentalResponse> getRentalsByOwner(String userEmail) {
        logger.info("Retrieving rentals for owner: {}", userEmail);
        
        try {
            // Find the user
            User owner = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", userEmail);
                        return new RuntimeException("User not found: " + userEmail);
                    });
            
            // Get rentals by owner
            List<Rental> rentals = rentalRepository.findByOwner(owner);
            logger.info("Found {} rentals for user: {}", rentals.size(), userEmail);
            
            return rentals.stream()
                    .map(RentalResponse::new)
                    .collect(Collectors.toList());
                    
        } catch (RuntimeException e) {
            throw e;  // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error retrieving rentals for user: {}", userEmail, e);
            throw new RuntimeException("Failed to retrieve user rentals");
        }
    }

    /**
     * Check if a user owns a specific rental
     * Utility method for permission checks
     */
    public boolean isRentalOwner(Long rentalId, String userEmail) {
        logger.debug("Checking if user {} owns rental {}", userEmail, rentalId);
        
        try {
            return rentalRepository.findById(rentalId)
                    .map(rental -> rental.getOwner().getEmail().equals(userEmail))
                    .orElse(false);
                    
        } catch (Exception e) {
            logger.error("Error checking rental ownership", e);
            return false;
        }
    }

    /**
     * Get rental statistics for a user
     * Useful for dashboard/analytics
     */
    public RentalStatistics getUserRentalStatistics(String userEmail) {
        logger.info("Getting rental statistics for user: {}", userEmail);
        
        try {
            User owner = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
            
            List<Rental> rentals = rentalRepository.findByOwner(owner);
            
            // Calculate statistics
            long totalRentals = rentals.size();
            double averagePrice = rentals.stream()
                    .mapToDouble(r -> r.getPrice().doubleValue())
                    .average()
                    .orElse(0.0);
            double totalSurface = rentals.stream()
                    .mapToDouble(r -> r.getSurface().doubleValue())
                    .sum();
            
            return new RentalStatistics(totalRentals, averagePrice, totalSurface);
            
        } catch (Exception e) {
            logger.error("Error calculating rental statistics for user: {}", userEmail, e);
            throw new RuntimeException("Failed to calculate rental statistics");
        }
    }

    /**
     * Validate rental request data
     * Centralized validation logic
     */
    private void validateRentalRequest(RentalRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Rental request cannot be null");
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Rental name is required");
        }
        
        if (request.getSurface() == null || request.getSurface().doubleValue() <= 0) {
            throw new IllegalArgumentException("Surface must be greater than 0");
        }
        
        if (request.getPrice() == null || request.getPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        
        logger.debug("Rental request validation passed for: {}", request.getName());
    }

    /**
     * Get rental entity by ID (internal use)
     * Used for retrieving existing rental data during updates
     */
    public Rental getRentalEntityById(Long id) {
        logger.debug("Retrieving rental entity with ID: {}", id);
        
        try {
            return rentalRepository.findById(id)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error retrieving rental entity with ID: {}", id, e);
            return null;
        }
    }
    public static class RentalStatistics {
        private final long totalRentals;
        private final double averagePrice;
        private final double totalSurface;
        
        public RentalStatistics(long totalRentals, double averagePrice, double totalSurface) {
            this.totalRentals = totalRentals;
            this.averagePrice = averagePrice;
            this.totalSurface = totalSurface;
        }
        
        // Getters
        public long getTotalRentals() { return totalRentals; }
        public double getAveragePrice() { return averagePrice; }
        public double getTotalSurface() { return totalSurface; }
    }
}