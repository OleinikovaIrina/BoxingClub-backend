# BoxingClub Backend

Spring Boot 3.5 based REST API for managing a boxing club.  
Implements secure authentication, membership lifecycle management and role-based access control.

## Overview

The system simulates real-world boxing club administration:

- User registration with email confirmation
- Stateless JWT-based authentication
- Role-based authorization
- Membership approval workflow
- Password reset via email token
- Administrative membership management

## Architecture

The project follows a layered architecture:

- `controller.interfaces` – API contracts (Swagger documented)
- `controller.impl` – REST controller implementations
- `service.interfaces` – business contracts
- `service.impl` – business logic
- `persistence` – JPA repositories
- `entity` – domain model
- `dto` – request and response models
- `security` – JWT configuration, filters, authentication setup
- `exception` – centralized exception handling

Controllers delegate business logic exclusively to services.  
Business rules are isolated from HTTP concerns.

## Membership Workflow

Membership lifecycle:


PENDING → APPROVED / REJECTED → CANCELLED

### Approval Process

When an administrator approves a membership:

- `status` changes to `APPROVED`
- `startDate` is set to the current date
- `endDate` is calculated based on selected duration:
    - `TRIAL`
    - `MONTHLY`
    - `YEARLY`

### Cancellation Process

When a membership is cancelled:

- `status` becomes `CANCELLED`
- `endDate` is updated to the current date (if needed)

### Active State

The membership active state is not stored in the database.

It is derived dynamically based on:

- `status == APPROVED`
- `startDate` and `endDate` are not null
- Current date is within the subscription period (inclusive)

## Security

- Stateless JWT authentication
- Role-based access control (`ROLE_ADMIN`, `ROLE_USER`)
- Method-level authorization using `@PreAuthorize`
- BCrypt password hashing
- HS256 secure JWT token generation
- Email confirmation token validation
- Password reset token lifecycle management

Example:

`@PreAuthorize("hasAuthority('ROLE_ADMIN')")`


## Technology Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- Spring Data JPA (Hibernate 6)
- MySQL 8
- MapStruct
- Lombok
- Mailtrap (development email testing)
- Gradle

## Environment Configuration

Sensitive values are externalized via environment variables.

### Required variables:

- JWT_SECRET
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- MAIL_USERNAME
- MAIL_PASSWORD

## Profiles:

dev
prod

## Running the Application
./gradlew bootRun

Default development port: 8081

## API Documentation

Swagger UI is available at:
/swagger-ui.html

## Notes

Passwords are stored using BCrypt hashing
JWT tokens are validated on every secured request
Business logic is enforced at the service layer
Membership activity is dynamically calculated based on status and subscription period