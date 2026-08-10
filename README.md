# BoxingClub Management System — Backend

A full-stack boxing club management application built with **Java 17, Spring Boot 3.5, React, TypeScript and PostgreSQL**.

The backend provides secure authentication, membership management, training scheduling, booking functionality, secure Telegram account linking, automated Telegram notifications, transactional email delivery and AI-based training advice.

The application is deployed on Render and includes a ready-to-use demo mode for exploring the main functionality without registration.

---

## Live Application

### Frontend

```text
https://boxingclub-frontend.onrender.com
```

### Backend API

```text
https://boxingclub-backend.onrender.com
```

### Swagger UI

```text
https://boxingclub-backend.onrender.com/swagger-ui.html
```

### Health Check

```text
https://boxingclub-backend.onrender.com/actuator/health
```

---

# Demo Mode

The deployed application provides dedicated demo access for exploring the system without registration or manually entering credentials.

Two demo roles are available from the frontend:

* **Demo User** — explore memberships, training sessions and booking functionality;
* **Demo Admin** — review memberships and manage trainers and training sessions.

The frontend performs demo authentication automatically when the corresponding demo button is selected.

Demo data includes:

* a demo user;
* a demo administrator;
* two demo trainers;
* an active membership for the demo user;
* recurring training sessions for approximately three months.

Demo data initialization is controlled through:

```text
APP_DEMO_DATA_ENABLED
```

When enabled, the backend creates the required demo data automatically.

Existing demo users, active memberships and training sessions are reused to avoid duplicate data during application restarts.

Demo passwords are read from environment variables when accounts are first created. Existing demo accounts retain their current passwords.

The demo user and admin passwords must match the credentials used by the frontend demo login.

---

# Main Features

## Authentication and Security

The application uses stateless JWT-based authentication with role-based authorization.

Implemented functionality includes:

* user registration;
* email confirmation;
* login with JWT access tokens;
* BCrypt password hashing;
* role-based authorization;
* password reset by email;
* protected API endpoints;
* centralized exception handling.

Supported roles:

```text
ROLE_USER
ROLE_ADMIN
ROLE_TRAINER
```

The backend is stateless and does not use server-side HTTP sessions for authentication.

---

## Membership Management

Users can:

* create a membership request;
* view their memberships;
* cancel a membership.

Administrators can:

* view pending membership requests;
* approve membership requests;
* reject membership requests;
* view active memberships.

Supported membership types:

```text
ADULT
CHILD
STUDENT
FAMILY
```

Supported membership durations:

```text
TRIAL
MONTHLY
YEARLY
```

Membership lifecycle:

```text
PENDING → APPROVED
        → REJECTED
        → CANCELLED
```

A membership is considered active when:

* its status is `APPROVED`;
* `startDate` and `endDate` are present;
* the current date is within the membership period.

The active state is calculated dynamically and is not stored as a separate database field.

A user must have an active membership to book training sessions.

---

# Training Session Management

Administrators can:

* create training sessions;
* edit training sessions;
* cancel training sessions;
* assign trainers;
* configure training duration;
* configure maximum participant capacity.

Supported session types:

```text
GROUP
INDIVIDUAL
```

Implemented business rules include:

* training sessions cannot be created in the past;
* assigned trainers must have `ROLE_TRAINER`;
* overlapping sessions for the same trainer are not allowed;
* duration is validated;
* participant capacity is validated;
* individual sessions allow only one participant;
* cancelled sessions cannot be booked.

---

# Booking Management

Users with an active membership can:

* view available future training sessions;
* search sessions by training title;
* search sessions by trainer;
* filter sessions by type;
* book available sessions;
* view upcoming bookings;
* cancel bookings.

Booking rules include:

* the membership must be active on the training date;
* duplicate bookings are not allowed;
* overlapping bookings for the same user are not allowed;
* group session capacity cannot be exceeded;
* individual sessions can only be booked by one user;
* cancelled training sessions cannot be booked;
* cancelled bookings remain stored in the database;
* booking cancellation is allowed only up to 24 hours before the training starts.

Concurrency-sensitive booking operations use **pessimistic database locking** together with additional capacity validation to prevent overbooking.

---

# Secure Telegram Integration

## Secure Account Linking

The application supports secure linking between an authenticated BoxingClub website account and a Telegram account.

The linking process does not require the frontend to send the user's email address or user ID.

### Linking Flow

1. The authenticated user requests a Telegram linking URL.
2. The backend identifies the user from the JWT authentication context.
3. A cryptographically secure one-time token is generated.
4. Only the SHA-256 hash of the token is stored in the database.
5. The backend returns a Telegram deep link.
6. The frontend opens the generated Telegram link.
7. The Telegram bot receives `/start <token>`.
8. The backend validates the token.
9. The token is consumed and cannot be reused.
10. The Telegram chat is linked to the authenticated BoxingClub account.

A Telegram account can be linked to only one BoxingClub user.

### Create Telegram Link

```http
POST /api/user/telegram/link
```

The endpoint:

* requires JWT authentication;
* does not require a request body;
* identifies the user from the authenticated security context;
* returns a Telegram deep link and token expiration information.

---

## Telegram Bot

The Telegram bot supports commands for:

* viewing available trainings;
* viewing available slots;
* searching trainings by trainer;
* viewing personal bookings;
* booking training sessions;
* receiving scheduled training reminders.

Conversation state is managed through an in-memory service based on:

```text
ConcurrentHashMap
```

Telegram notifications are sent automatically before scheduled training sessions.

The Telegram bot can be enabled or disabled through an environment variable.

---

# AI Training Advice

The backend includes integration with an external LLM API for boxing-related training advice.

The AI service uses Spring `WebClient` to communicate with the Groq API and returns generated training recommendations to the frontend.

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

The Groq API key is supplied through an environment variable and is never stored in the repository.

---

# Email Delivery

Transactional email is implemented through **Brevo SMTP**.

Email functionality includes:

* account confirmation emails;
* password reset emails;
* HTML email support;
* configurable sender address;
* environment-based SMTP credentials.

Email confirmation links are valid for 90 minutes.

---

# Architecture

The backend follows a layered architecture with separation between API contracts, controllers, business logic, persistence and infrastructure concerns.

Main packages include:

```text
controller.interfaces   API contracts
controller.impl         REST controller implementations
service.interfaces      Service contracts
service.impl            Business logic
persistence             Spring Data JPA repositories
entity                   Domain entities
dto                      Request and response models
security                 JWT authentication and authorization
exception                Centralized exception handling
telegram                 Telegram bot, secure linking and notifications
mail                     Transactional email delivery
config.demo              Demo data initialization
```

Controllers delegate application logic to services.

Business rules are implemented in the service layer and remain separated from HTTP concerns.

Spring Data JPA repositories handle persistence, while security, email, Telegram and AI integrations are separated into dedicated modules.

---

# Technology Stack

## Backend

* Java 17
* Spring Boot 3.5
* Spring Security
* JWT
* Spring Data JPA
* Hibernate 6
* Spring Web
* Spring WebClient
* Jakarta Validation
* MapStruct
* Lombok

## Frontend

* React
* TypeScript
* Vite
* React Router
* Axios
* Formik
* Yup
* Tailwind CSS

## Databases

* MySQL 8 — local development
* H2 — tests and development scenarios
* PostgreSQL — deployed application

## Integrations

* Telegram Bots API
* Groq API
* Brevo SMTP

## Build, Testing and Deployment

* Gradle
* Docker
* JUnit
* Mockito
* Git
* GitHub
* GitLab
* Render

---

# Environment Configuration

Sensitive configuration values are externalized through environment variables.

Secrets and credentials must never be committed to GitHub or GitLab.

## Application and Profiles

```text
SPRING_PROFILES_ACTIVE
BACKEND_URL
FRONTEND_URL
```

Production profile:

```text
SPRING_PROFILES_ACTIVE=prod,brevo
```

---

## Database

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
```

---

## JWT

```text
JWT_SECRET
JWT_ACCESS_EXP_MIN
```

---

## Brevo SMTP

```text
BREVO_SMTP_LOGIN
BREVO_SMTP_KEY
MAIL_FROM
```

`BREVO_SMTP_KEY` must contain only the SMTP key value.

Credentials are configured through the deployment environment and are not stored in the repository.

---

## Telegram

```text
TELEGRAM_BOT_USERNAME
TELEGRAM_BOT_TOKEN
TELEGRAM_BOT_ENABLED
TELEGRAM_LINK_TOKEN_EXPIRATION_MINUTES
```

---

## AI Integration

```text
GROQ_API_KEY
```

---

## Demo Mode

```text
APP_DEMO_DATA_ENABLED
DEMO_USER_PASSWORD
DEMO_ADMIN_PASSWORD
DEMO_TRAINER_PASSWORD
```

Demo password values are configured through the deployment environment.

The values used for the demo user and demo administrator must correspond to the credentials used by the frontend demo login.

---

## JVM Configuration

The deployed backend can use:

```text
JAVA_TOOL_OPTIONS
```

to configure JVM memory usage for the selected hosting instance.

---

# Spring Profiles

The project supports environment-specific Spring profiles.

Available profiles include:

```text
dev
prod
brevo
mailtrap
```

The deployed application uses:

```text
prod,brevo
```

---

# Running the Project Locally

## Backend

Requirements:

* Java 17
* MySQL 8

Configure the required environment variables listed above before starting the backend.

Depending on the selected Spring profile, additional configuration such as JWT, AI integration and mail settings may be required.

### Linux / macOS

```bash
./gradlew bootRun
```

### Windows

```bash
gradlew.bat bootRun
```

The local backend runs on:

```text
http://localhost:8081
```

---

## Frontend

The React frontend is maintained separately from the Spring Boot backend.

From the frontend project directory, install dependencies:

```bash
npm install
```

Start the Vite development server:

```bash
npm run dev
```

The frontend development server runs by default on:

```text
http://localhost:5173
```

During local development, the frontend communicates with the backend at:

```text
http://localhost:8081
```

---

# Running Tests

Run the complete backend test suite.

### Linux / macOS

```bash
./gradlew test
```

### Windows

```bash
gradlew.bat test
```

Run a clean test build.

### Linux / macOS

```bash
./gradlew clean test
```

### Windows

```bash
gradlew.bat clean test
```

---

# API Documentation

Swagger/OpenAPI documentation is available through Springdoc.

## Local Swagger UI

```text
http://localhost:8081/swagger-ui.html
```

## Production Swagger UI

```text
https://boxingclub-backend.onrender.com/swagger-ui.html
```

---

# Health Check

## Local

```text
http://localhost:8081/actuator/health
```

## Production

```text
https://boxingclub-backend.onrender.com/actuator/health
```

Expected health response:

```json
{
  "status": "UP"
}
```

---

# Production Deployment

The application is deployed on **Render**.

Production infrastructure includes:

* Render Web Service for the Spring Boot backend;
* Render Static Site for the React frontend;
* Render PostgreSQL database;
* Docker-based backend deployment;
* Brevo SMTP for transactional email;
* Groq API for AI training advice;
* Telegram Bot API integration.

Production URLs:

```text
Frontend:
https://boxingclub-frontend.onrender.com

Backend:
https://boxingclub-backend.onrender.com

Swagger:
https://boxingclub-backend.onrender.com/swagger-ui.html

Health:
https://boxingclub-backend.onrender.com/actuator/health
```

The backend uses the `PORT` value provided by Render automatically.

Sensitive deployment configuration is supplied through Render environment variables.

---

# Future Development

The main planned extension is a **personalized AI training assistant**.

The goal is to extend the current AI integration so that recommendations can use authenticated user context, such as:

* membership information;
* training history;
* upcoming bookings;
* available training sessions.

This would allow the AI assistant to provide individual training recommendations based on the user's actual BoxingClub activity instead of answering only general boxing questions.
