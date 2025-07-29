# ChâTop - Backend API

## Description

ChâTop is a real estate rental application that allows users to publish and browse rental listings. This REST API backend provides all the necessary features for managing users, rentals, and messaging system.

## Technologies Used

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (ORM)
- **MySQL** (Database)
- **Maven** (Dependency Management)
- **SpringDoc OpenAPI** (Swagger Documentation)

## Features

- ✅ **JWT Authentication** (Register, Login)
- ✅ **User Management** (User profiles)
- ✅ **Rental CRUD** (Create, read, update rental listings)
- ✅ **Image Upload** (Rental property photos)
- ✅ **Messaging System** (User communication)
- ✅ **API Documentation** (Swagger UI)
- ✅ **Centralized Error Handling**
- ✅ **Data Validation**

## Installation

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **MySQL 8.0+**
- **Git**

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Gautier-DC/P3_Developpez_le_backend_en_utilisant_Java_et_Spring.git
   cd P3_Developpez_le_backend_en_utilisant_Java_et_Spring
   ```

2. **Setup the database**
   ```sql
   cd database
   CREATE DATABASE chatop;
   CREATE USER 'chatop_user'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON chatop.* TO 'chatop_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure application.properties**
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/chatop
   spring.datasource.username=chatop_user
   spring.datasource.password=password
   
   # JWT Configuration
   jwt.secret=your-very-long-and-secure-jwt-secret
   jwt.expiration=86400000
   
   # File Upload Configuration
   file.upload-dir=./uploads/images
   spring.servlet.multipart.max-file-size=10MB
   spring.servlet.multipart.max-request-size=10MB
   ```

4. **Install dependencies and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The application will be accessible at `http://localhost:3001`

## API Documentation

Once the application is running, Swagger documentation is available at:

- **Swagger UI**: `http://localhost:3001/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:3001/v3/api-docs`

## Authentication

The API uses JWT authentication. To access protected endpoints:

1. **Register**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login`
3. **Use the token** in header: `Authorization: Bearer <your-token>`

## Project Structure

```
src/main/java/com/openclassrooms/chatop/
├── config/              # Spring Security, CORS configuration
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
├── entity/              # JPA Entities
├── exception/           # Exception handling
├── repository/          # JPA Repositories
├── security/            # JWT, security filters
└── service/             # Business logic
```

## Data Model

### Main Entities

- **User**: Application users
- **Rental**: Rental property listings
- **Message**: Messages between users

### Relationships

- `User` ↔ `Rental` (1:N) - A user can have multiple rentals
- `User` ↔ `Message` (1:N) - A user can send multiple messages
- `Rental` ↔ `Message` (1:N) - A rental can have multiple messages

## Security

- **JWT** for stateless authentication
- **BCrypt** for password hashing (strength 12)
- **CORS** configured for frontend
- **Input validation** for all endpoints
- **Centralized** and secure error handling

## Error Handling

The API returns JSON formatted errors with specific codes:

```json
{
  "message": "Error description",
  "code": "DOMAIN_HTTP_CODE",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

### Error Codes

- `AUTH_401` - Authentication error
- `AUTH_400` - Invalid registration data
- `RENTAL_404` - Rental not found
- `RENTAL_403` - Not authorized to modify this rental
- `UPLOAD_413` - File too large
- `SERVER_500` - Internal server error

## 🔧 Environment Configuration

### Development
```properties
spring.profiles.active=dev
logging.level.com.openclassrooms.chatop=DEBUG
```

### Production
```properties
spring.profiles.active=prod
logging.level.com.openclassrooms.chatop=INFO
server.port=8080
```

## Performance

- **File uploads**: Limited to 10MB max
- **JWT**: Configured with 24h expiration
- **Database**: Indexed on frequently used fields

## Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Author

**Gautier DC**

## Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/guides/gs/securing-web/)
- [JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

---

**This project is part of the OpenClassrooms fullstack program**