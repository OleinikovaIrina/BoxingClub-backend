# BoxingClub Backend

REST API for managing a boxing club, built with Java 17 and Spring Boot 3.5.

The application provides secure authentication, membership management, training scheduling, booking functionality, Telegram account linking, notifications and AI-based training advice.

## Project Status

The backend is deployed on Render and connected to a PostgreSQL production database.

Final production smoke testing is currently in progress.

### Live Application

Frontend:

```text
https://boxingclub-frontend.onrender.com
```

Backend API:

```text
https://boxingclub-backend.onrender.com
```

Health check:

```text
https://boxingclub-backend.onrender.com/actuator/health
```

Swagger UI:

```text
https://boxingclub-backend.onrender.com/swagger-ui.html
```

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

The Telegram bot can be enabled or disabled through an environment variable.

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

## Email Delivery

Transactional emails are sent through Brevo SMTP.

Email functionality includes:

- account confirmation emails;
- password reset emails;
- HTML email support;
- configurable sender address;
- environment-based SMTP credentials.

Email confirmation links are valid for 90 minutes.

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

On Render, the corresponding environment variable is:

```text
APP_DEMO_DATA_ENABLED=true
```

Existing demo users, memberships and training sessions are reused to prevent duplicate data during application restart.

Demo data is currently disabled while final production testing is being completed.

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
- `mail` — transactional email delivery;
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
- MySQL 8 for local development
- PostgreSQL for production
- MapStruct
- Lombok
- Spring WebClient
- Telegram Bots API
- Groq API
- Brevo SMTP
- Gradle
- Docker
- JUnit
- Mockito
- Render

## Environment Configuration

Sensitive configuration values are externalized through environment variables.

### Application and Profiles

```text
SPRING_PROFILES_ACTIVE
BACKEND_URL
FRONTEND_URL
```

Production profile example:

```text
SPRING_PROFILES_ACTIVE=prod,brevo
```

### Database

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
```

### JWT

```text
JWT_SECRET
JWT_ACCESS_EXP_MIN
```

### Brevo SMTP

```text
BREVO_SMTP_LOGIN
BREVO_SMTP_KEY
MAIL_FROM
```

`BREVO_SMTP_KEY` must contain only the SMTP key value.

It must not contain the environment variable name or an API key.

### Telegram

```text
TELEGRAM_BOT_USERNAME
TELEGRAM_BOT_TOKEN
TELEGRAM_BOT_ENABLED
TELEGRAM_LINK_TOKEN_EXPIRATION_MINUTES
```

### AI Integration

```text
GROQ_API_KEY
```

### Demo Data

```text
APP_DEMO_DATA_ENABLED
```

### JVM Configuration

The production deployment can use `JAVA_TOOL_OPTIONS` to configure JVM memory usage for the selected hosting instance.

Secrets and credentials must never be committed to GitHub or GitLab.

## Spring Profiles

The project supports environment-specific Spring profiles.

Available profiles include:

```text
dev
prod
brevo
mailtrap
```

Production deployment uses:

```text
prod,brevo
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

Run a clean test build:

```bash
./gradlew clean test
```

## API Documentation

Local Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

Production Swagger UI:

```text
https://boxingclub-backend.onrender.com/swagger-ui.html
```

Local health endpoint:

```text
http://localhost:8081/actuator/health
```

Production health endpoint:

```text
https://boxingclub-backend.onrender.com/actuator/health
```

Expected production health response:

```json
{
  "status": "UP",
  "groups": [
    "liveness",
    "readiness"
  ]
}
```

## Production Deployment

The application is deployed on Render.

Production infrastructure:

- Render Web Service for the backend;
- Render Static Site for the frontend;
- Render PostgreSQL database;
- Docker-based backend deployment;
- Brevo SMTP for transactional email;
- Groq API for AI training advice.

Production URLs:

```text
Frontend:
https://boxingclub-frontend.onrender.com

Backend:
https://boxingclub-backend.onrender.com

Health:
https://boxingclub-backend.onrender.com/actuator/health

Swagger:
https://boxingclub-backend.onrender.com/swagger-ui.html
```

The backend service uses the Render-provided `PORT` value automatically.

## Planned Improvements

- add personalized AI training recommendations;
- add AI tools for finding suitable sessions and creating bookings;
- integrate the personalized AI service with Telegram;
- improve training and booking history presentation;
- add attendance tracking;
- optimize selected database queries;
- introduce database migrations with Flyway;
- add additional integration and production tests.