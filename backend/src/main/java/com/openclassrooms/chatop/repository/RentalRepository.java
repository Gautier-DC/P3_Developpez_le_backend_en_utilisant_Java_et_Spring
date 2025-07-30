package com.openclassrooms.chatop.repository;

import com.openclassrooms.chatop.entity.Rental;
import com.openclassrooms.chatop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}