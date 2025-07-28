package com.openclassrooms.chatop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration for Chatop API
 * Provides comprehensive API documentation with JWT authentication
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure OpenAPI documentation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .tags(apiTags())
                .addSecurityItem(securityRequirement())
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("JWT", jwtSecurityScheme()));
    }

    /**
     * API Information
     */
    private Info apiInfo() {
        return new Info()
                .title("Chatop API")
                .description("""
                    ## Chatop - Rental Property Management Platform
                    
                    A comprehensive REST API for managing rental properties with user authentication and messaging system.
                    
                    ### Features:
                    - **JWT Authentication** - Secure user registration and login
                    - **Rental Management** - CRUD operations for rental properties
                    - **Messaging System** - Communication between users and property owners
                    - **File Upload** - Image upload for rental properties
                    
                    ### Authentication:
                    Most endpoints require JWT authentication. Use the `/api/auth/login` endpoint to obtain a token.
                    
                    ### Getting Started:
                    1. Register a new user with `/api/auth/register`
                    2. Login to get your JWT token with `/api/auth/login`
                    3. Include the token in the Authorization header: `Bearer <your-token>`
                    4. Start creating rentals and sending messages!
                    """)
                .version("1.0.0")
                .contact(apiContact())
                .license(apiLicense());
    }

    /**
     * API Contact Information
     */
    private Contact apiContact() {
        return new Contact()
                .name("Chatop Development Team")
                .email("dev@chatop.com")
                .url("https://github.com/your-username/chatop");
    }

    /**
     * API License
     */
    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * API Servers
     */
    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:3001")
                        .description("Development Server")
        );
    }

    /**
     * API Tags for organizing endpoints
     */
    private List<Tag> apiTags() {
        return List.of(
                new Tag()
                        .name("Authentication")
                        .description("User registration, login, and profile management"),
                new Tag()
                        .name("Rentals")
                        .description("Rental property management - CRUD operations"),
                new Tag()
                        .name("Messages")
                        .description("Messaging system between users and property owners"),
                new Tag()
                        .name("Files")
                        .description("File upload and management for rental images")
        );
    }

    /**
     * Security requirement for JWT
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("JWT");
    }

    /**
     * JWT Security Scheme
     */
    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token for API authentication. Format: Bearer <token>");
    }
}
