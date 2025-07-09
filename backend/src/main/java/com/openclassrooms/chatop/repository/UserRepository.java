package com.openclassrooms.chatop.repository;

import com.openclassrooms.chatop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by their email
     * Used for authentication
     *
     * @param email The user's email
     * @return Optional<User> The user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if an email already exists
     *
     * @param email The email to check
     * @return true if the email already exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find a user by their name
     *
     * @param name The user's name
     * @return Optional<User> The user if found
     */
    Optional<User> findByName(String name);
    
    /**
     * Find users whose name contains the given string
     * Case insensitive search
     *
     * @param name Part of the name to search
     * @return List of matching users
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    java.util.List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Count the number of users
     * Method inherited from JpaRepository but documented here
     *
     * @return Total number of users
     */
    // count() is already available via JpaRepository

    /**
     * Find a user by their email and password
     * Note: In production, use findByEmail then check the hash
     *
     * @param email The user's email
     * @param password The user's password (hashed)
     * @return Optional<User> The user if found
     */
    Optional<User> findByEmailAndPassword(String email, String password);
}

/*
 * Méthodes automatiquement disponibles via JpaRepository<User, Long> :
 * 
 * save(User user) - Sauvegarde ou met à jour un utilisateur
 * findById(Long id) - Trouve un utilisateur par son ID
 * findAll() - Récupère tous les utilisateurs
 * deleteById(Long id) - Supprime un utilisateur par son ID
 * delete(User user) - Supprime un utilisateur
 * count() - Compte le nombre d'utilisateurs
 * existsById(Long id) - Vérifie si un utilisateur existe par son ID
 * 
 * Exemples d'utilisation :
 * 
 * // Créer un utilisateur
 * User user = new User("john@example.com", "John Doe", "password123");
 * userRepository.save(user);
 * 
 * // Trouver par email (pour login)
 * Optional<User> user = userRepository.findByEmail("john@example.com");
 * 
 * // Vérifier si email existe (pour register)
 * if (userRepository.existsByEmail("john@example.com")) {
 *     // Email déjà utilisé
 * }
 * 
 * // Lister tous les utilisateurs
 * List<User> allUsers = userRepository.findAll();
 */