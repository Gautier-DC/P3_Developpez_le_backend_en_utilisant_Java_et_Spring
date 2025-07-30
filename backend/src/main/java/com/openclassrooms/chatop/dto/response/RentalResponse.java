package com.openclassrooms.chatop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openclassrooms.chatop.entity.Rental;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for rental response data
 * Used to send rental information to clients
 */
public class RentalResponse {

    @Schema(description = "ID of the rental", example = "1")
    private Long id;

    @Schema(description = "Name of the rental", example = "Cozy Apartment")
    private String name;

    @Schema(description = "Surface of the rental", example = "150")
    private BigDecimal surface;

    @Schema(description = "Price of the rental", example = "1200.00")
    private BigDecimal price;

    @Schema(description = "Picture URL of the rental", example = "http://example.com/picture.jpg")
    private String picture;

    @Schema(description = "Description of the rental", example = "A cozy apartment in the city center with all amenities.")
    private String description;

    @Schema(description = "ID of the rental owner", example = "1")
    @JsonProperty("owner_id")
    private Long ownerId;

    @Schema(description = "Creation date of the rental", example = "2023-01-01T12:00:00")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "Last update date of the rental", example = "2023-01-01T12:00:00")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public RentalResponse() {
    }

    // Constructor from Rental entity
    public RentalResponse(Rental rental) {
        this.id = rental.getId();
        this.name = rental.getName();
        this.surface = rental.getSurface();
        this.price = rental.getPrice();
        this.picture = rental.getPicture();
        this.description = rental.getDescription();
        this.ownerId = rental.getOwner().getId();
        this.createdAt = rental.getCreatedAt();
        this.updatedAt = rental.getUpdatedAt();
    }

    // Constructor with parameters
    public RentalResponse(Long id, String name, BigDecimal surface, BigDecimal price,
            String picture, String description, Long ownerId, String ownerName,
            String ownerEmail, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.surface = surface;
        this.price = price;
        this.picture = picture;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Static factory method
    public static RentalResponse fromEntity(Rental rental) {
        return new RentalResponse(rental);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSurface() {
        return surface;
    }

    public void setSurface(BigDecimal surface) {
        this.surface = surface;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "RentalResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surface=" + surface +
                ", price=" + price +
                ", picture='" + picture + '\'' +
                ", description='" + description + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
