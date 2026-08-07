# BoxingClub Backend

REST API for managing a boxing club, built with Java 17 and Spring Boot 3.5.

The application provides secure authentication, membership management, training scheduling, booking functionality, Telegram account linking, notifications and AI-based training advice.

## Project Status

The backend is fully implemented and tested locally.

Production deployment is currently in preparation.

Local backend URL:

```text
http://localhost:8081
```

## Main Features

### Authentication and Security

- User registration
- Email confirmation
- Stateless JWT authentication
- Role-based authorization
- BCrypt password hashing
- Password reset by email
- Centralized exception handling
- Protected API endpoints

Supported roles:

- `ROLE_USER`
- `ROLE_ADMIN`
- `ROLE_TRAINER`

### Membership Management

Users can:

- create a membership request;
- view their memberships;
- cancel a membership.

Administrators can:

- view pending memberships;
- approve membership requests;
- reject membership requests;
- view active memberships.

Supported membership types:

- `ADULT`
- `CHILD`
- `STUDENT`
- `FAMILY`

Supported durations:

- `TRIAL`
- `MONTHLY`
- `YEARLY`

Membership lifecycle:

```text
PENDING → APPROVED
        → REJECTED
        → CANCELLED
```

A membership is considered active when:

- its status is `APPROVED`;
- `startDate` and `endDate` are present;
- the current date is within the membership period.

The active state is calculated dynamically and is not stored as a separate database field.

## Training Sessions

Administrators can:

- create training sessions;
- edit training sessions;
- cancel training sessions;
- assign trainers;
- configure session duration;
- configure maximum participant capacity.

Supported session types:

- `GROUP`
- `INDIVIDUAL`

Business rules include:

- sessions cannot be created in the past;
- a trainer must have the `ROLE_TRAINER` authority;
- trainer schedule overlaps are not allowed;
- individual sessions can have only one participant;
- cancelled sessions cannot be booked;
- session capacity is checked before booking.

## Booking Management

Users with an active membership can:

- view available future training sessions;
- search sessions by training title;
- search sessions by trainer;
- filter sessions by type;
- book available sessions;
- view their upcoming bookings;
- cancel their bookings.

Booking rules include:

- an active membership must be valid on the training date;
- duplicate bookings are not allowed;
- overlapping user bookings are not allowed;
- group session capacity cannot be exceeded;
- individual sessions can only be booked by one user;
- cancelled bookings remain in the database;
- booking cancellation is allowed only up to 24 hours before the training starts.

Concurrency-sensitive booking operations use pessimistic locking and additional capacity validation.

## Secure Telegram Linking

The application supports secure linking between a website account and a Telegram account.

Linking flow:

1. The authenticated user requests a Telegram linking URL.
2. The backend identifies the user from the JWT token.
3. A cryptographically secure one-time token is generated.
4. Only the SHA-256 hash of the token is stored in the database.
5. The frontend opens the generated Telegram deep link.
6. The Telegram bot receives `/start <token>`.
7. The backend validates and consumes the token.
8. The Telegram chat is linked to the authenticated website account.

The frontend does not send an email address or user ID during the linking process.

A Telegram account can be linked to only one BoxingClub user.

### Create Telegram Link

```http
POST /api/user/telegram/link
```

The endpoint:

- requires JWT authentication;
- does not require a request body;
- returns a Telegram deep link and expiration time.

## Telegram Bot

The Telegram bot supports commands for:

- viewing available trainings;
- viewing available slots;
- searching by trainer;
- viewing personal bookings;
- booking a training;
- receiving scheduled reminders.

The bot uses an in-memory conversation state service based on `ConcurrentHashMap`.

Telegram notifications are sent before scheduled training sessions.

## AI Training Advice

The backend includes an experimental AI integration for boxing-related training advice.

The application sends user questions to the Groq API using Spring `WebClient` and returns the generated answer.

### Endpoint

```http
POST /api/ai/training-advice
```

Example request:

```json
{
  "question": "How can I improve my boxing stamina?"
}
```

Example response:

```json
{
  "answer": "Focus on interval training, jump rope sessions and structured sparring rounds."
}
```

The Groq API key must be provided through an environment variable.

Personalized AI recommendations are planned as a future improvement.

## Demo Data

The application contains an optional demo data initializer.

It can create:

- a demo user;
- a demo administrator;
- two demo trainers;
- an active demo membership;
- recurring training sessions for approximately three months.

The initializer is enabled only when the following property is set:

```properties
app.demo-data.enabled=true
```

Existing demo users, memberships and training sessions are reused to prevent duplicate data during application restart.

## Architecture

The project follows a layered architecture:

- `controller.interfaces` — API contracts;
- `controller.impl` — REST controller implementations;
- `service.interfaces` — service contracts;
- `service.impl` — business logic;
- `persistence` — Spring Data JPA repositories;
- `entity` — domain entities;
- `dto` — request and response models;
- `security` — JWT configuration and authentication filters;
- `exception` — centralized exception handling;
- `telegram` — Telegram bot, linking and notification logic;
- `config.demo` — optional demo data initialization.

Controllers delegate business logic to services.

Business rules are implemented in the service layer and are separated from HTTP concerns.

## Technology Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- JWT
- Spring Data JPA
- Hibernate 6
- MySQL 8
- PostgreSQL for production deployment
- MapStruct
- Lombok
- Spring WebClient
- Telegram Bots API
- Groq API
- Gradle
- JUnit
- Mockito

## Environment Configuration

Sensitive configuration values are externalized through environment variables.

Typical required variables include:

```text
JWT_SECRET
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
MAIL_USERNAME
MAIL_PASSWORD
TELEGRAM_BOT_TOKEN
TELEGRAM_BOT_USERNAME
GROQ_API_KEY
```

The exact email variables depend on the active email profile.

## Spring Profiles

The project supports environment-specific Spring profiles.

Examples:

```text
dev
prod
brevo
```

## Running Locally

Start the application:

```bash
./gradlew bootRun
```

Default local port:

```text
8081
```

Local API URL:

```text
http://localhost:8081
```

## Running Tests

Run all backend tests:

```bash
./gradlew test
```

## API Documentation

When the application is running locally, Swagger UI is available at:

```text
http://localhost:8081/swagger-ui.html
```

Health endpoint:

```text
http://localhost:8081/actuator/health
```

## Production Deployment

Production deployment is in preparation.

After deployment, this section will contain:

- production backend URL;
- production Swagger URL;
- health-check URL;
- frontend URL;
- demo credentials.

## Planned Improvements

- add personalized AI training recommendations;
- improve training and booking history presentation;
- add attendance tracking;
- optimize selected database queries;
- complete production deployment and configuration.