package com.openclassrooms.chatop.repository;

import com.openclassrooms.chatop.entity.Rental;
import com.openclassrooms.chatop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    
    /**
     * Find a rental by its ID
     * Used to display "My Rentals"
     *
     * @param id The rental ID
     * @return Optional<Rental> The rental if found
     */
    Optional<Rental> findById(Long id);
    List<Rental> findByOwner(User owner);
    
    /**
     * Find all rentals by owner ID
     * Alternative more convenient when only the ID is available
     *
     * @param ownerId The owner's ID
     * @return List of the owner's rentals
     */
    List<Rental> findByOwnerId(Long ownerId);
    
    /**
     * Find rentals by name (case insensitive partial search)
     * Used for rental search
     *
     * @param name Part of the name to search
     * @return List of matching rentals
     */
    @Query("SELECT r FROM Rental r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Rental> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find rentals with a price less than or equal to the given amount
     * Used to filter by budget
     *
     * @param maxPrice Maximum price
     * @return List of rentals within the budget
     */
    List<Rental> findByPriceLessThanEqual(BigDecimal maxPrice);
    
    /**
     * Find rentals with a price between two amounts
     * Used to filter by price range
     *
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of rentals within the range
     */
    List<Rental> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find rentals with a minimum surface area
     * Used to filter by size
     *
     * @param minSurface Minimum surface area
     * @return List of rentals with surface >= minSurface
     */
    List<Rental> findBySurfaceGreaterThanEqual(BigDecimal minSurface);
    
    /**
     * Find rentals created after a given date
     * Used to display "New Rentals"
     *
     * @param date Reference date
     * @return List of recent rentals
     */
    List<Rental> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Advanced search: rentals by name and price range
     * Combine multiple search criteria
     *
     * @param name Part of the name
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of matching rentals
     */
    @Query("SELECT r FROM Rental r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND r.price BETWEEN :minPrice AND :maxPrice")
    List<Rental> findByNameAndPriceRange(@Param("name") String name,
                                        @Param("minPrice") BigDecimal minPrice,
                                        @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Find rentals with a description containing keywords
     * Used for content search
     *
     * @param keyword Keyword to search
     * @return List of matching rentals
     */
    @Query("SELECT r FROM Rental r WHERE LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Rental> findByDescriptionContainingIgnoreCase(@Param("keyword") String keyword);
    
    /**
     * Count the number of rentals for a given owner
     * Used for statistics
     *
     * @param owner The owner
     * @return Number of rentals for the owner
     */
    long countByOwner(User owner);
    
    /**
     * Find the most recent rentals (limited)
     * Used for the homepage
     *
     * @return List of recent rentals
     */
    @Query("SELECT r FROM Rental r ORDER BY r.createdAt DESC LIMIT 10")
    List<Rental> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * Check if a rental exists with this name for this owner
     * Prevent duplicates for the same owner
     *
     * @param name Name of the rental
     * @param owner Owner
     * @return true if a rental with this name exists for this owner
     */
    boolean existsByNameAndOwner(String name, User owner);
}