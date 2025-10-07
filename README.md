# BoxingClub

A modern Spring Boot-based backend for managing boxing club activities in Germany.  
This project simulates the real-world management of a boxing club, including users, classes, coaches, and scheduling.

## Features (MVP)

- User registration and confirmation (with email token)
- Login & JWT-based authentication (roles: `ADMIN`, `COACH`, `MEMBER`)
- Password reset flow (token generation & validation)
- Membership management (approve/reject by admin)
- Class scheduling (create/update boxing classes)
- Booking system for members
- Coach and user profile management
- Global exception handling & validation

## Tech Stack

- **Java 17**
- **Spring Boot 3.5** (Web, Security, Data JPA, Validation, Mail)
- **Hibernate/JPA** + **MySQL**
- **MapStruct**, **Lombok**
- **Springdoc OpenAPI** (Swagger UI for API documentation)
- Build: **Gradle**
- Tests: **JUnit 5, Mockito**
- Docker-ready (MySQL container)

## Project Structure

To be documented in the next update.

