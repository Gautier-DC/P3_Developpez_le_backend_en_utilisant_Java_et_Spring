package com.openclassrooms.chatop.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * DTO for rental creation and update requests
 * Contains validation annotations for data integrity
 */
public class RentalRequest {

    @NotBlank(message = "Rental name is required")
    @Size(min = 2, max = 100, message = "Rental name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Surface is required")
    @DecimalMin(value = "1.0", message = "Surface must be greater than 0")
    @DecimalMax(value = "10000.0", message = "Surface must be less than 10000 square meters")
    private BigDecimal surface;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.0", message = "Price must be greater than 0")
    @DecimalMax(value = "1000000.0", message = "Price must be less than 1,000,000")
    private BigDecimal price;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @Size(max = 255, message = "Picture URL must be less than 255 characters")
    private String picture;

    // Default constructor
    public RentalRequest() {
    }

    // Constructor with parameters
    public RentalRequest(String name, BigDecimal surface, BigDecimal price, String description, String picture) {
        this.name = name;
        this.surface = surface;
        this.price = price;
        this.description = description;
        this.picture = picture;
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "RentalRequest{" +
                "name='" + name + '\'' +
                ", surface=" + surface +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}