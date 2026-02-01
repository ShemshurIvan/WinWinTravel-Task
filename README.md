# WinWin Travel Microservices Task

This project consists of two Spring Boot microservices (`auth-api` and `data-api`) and a PostgreSQL database, orchestrating a secure text processing flow using Docker Compose.

## 🚀 Project Overview

* **Auth API (Port 8080):** Handles user registration, JWT authentication, and request logging. Acts as the gateway.
* **Data API (Port 8081):** A protected internal service that performs text transformation (Reversing & Uppercasing).
* **PostgreSQL (Port 5432):** Persists user credentials and processing logs.

## 📋 Prerequisites

* **Docker Desktop** (Running)
* **Postman** (For API testing)
* **Java 21** (Provided via Maven Wrapper)

---

## 🛠️ Setup & Run

### 1. Clean Build
Because this project uses **Lombok 1.18.36** and specific internal networking, you **must** package the JARs locally before starting Docker to ensure the latest bytecode is used.

Run these commands from the project root:

```bash
# Package Service A (Auth)
cd auth-api && ./mvnw clean package -Dmaven.test.skip=true && cd ..

# Package Service B (Data)
cd data-api && ./mvnw clean package -Dmaven.test.skip=true && cd ..

```
### 2. Launch the Environment

Start the container orchestration. This will create the network winwin-net, initialize the database, and start both services.

```bash

docker compose up --build

```

Wait for the logs to show "Started AuthApiApplication" before testing.


## 🔌 API Documentation

All public interactions occur via the Auth API on port 8080.

### 1. Register a New User

Creates a new user account with a BCrypt-hashed password.

Endpoint: POST /api/auth/register

URL: http://localhost:8080/api/auth/register

Body (JSON):

```bash

{
  "email": "user@winwin.com",
  "password": "securepassword"
}
```
Response: 201 Created - "User registered".

### 2. Login

Authenticates the user and returns a session token.

Endpoint: POST /api/auth/login

URL: http://localhost:8080/api/auth/login

Body (JSON): Same as registration.

Response: 200 OK

```bash

{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 3.Process Text (Secure Integration)

his endpoint demonstrates the full microservice flow: Auth -> Validation -> DB Log -> Data API -> Result.

Endpoint: POST /api/process

URL: http://localhost:8080/api/process

Headers:

Authorization: Bearer <PASTE_YOUR_TOKEN_HERE>

Body (JSON):

```bash

{
  "text": "WinWin Travel"
}
```
Response: 200 OK

```bash

{
  "result": "LEVART NIWNIW"
}
```

## 💾 Database Verification

You can connect to the PostgreSQL database to verify that data is being persisted correctly.

* **Host**: `localhost`
* **Port**: `5432`
* **Database**: `appdb`
* **Username**: `user`
* **Password**: `password`

### Key Tables
1.  **`users`**: Stores registered accounts.
    * Columns: `id`, `email`, `password_hash`.
2.  **`processing_log`**: Audit trail of all text transformations.
    * Columns: `id`, `user_id`, `input_text`, `output_text`, `created_at`.

### Useful SQL Queries
```sql
-- View all registered users
SELECT * FROM users;

-- View the history of processed text
SELECT * FROM processing_log ORDER BY created_at DESC;
```


## ⚙️ Configuration & Environment

The application uses the following configuration, synchronized between `docker-compose.yml` and `application.properties` to ensure seamless communication between containers.

| Variable | Value | Description |
| :--- | :--- | :--- |
| **Service A URL** | `http://winwin-data:8081` | Internal DNS name used by Auth API to reach Data API. |
| **JWT Secret** | `404E63...` | Key used to sign user session tokens. |
| **Internal Token** | `secret-internal-token` | Shared secret for server-to-server authentication. |
| **DB Connection** | `jdbc:postgresql://winwin-postgres:5432/appdb` | Connection string for the Auth API to reach the database container. |

---

## 🛠️ Troubleshooting

If you encounter issues during setup or execution, refer to the following common resolutions:

### 1. "Connection Refused" / "Unknown Host"
* **Symptom**: The Auth API throws a 500 error when calling the process endpoint.
* **Cause**: `auth-api` is trying to connect to `localhost` instead of the Docker service alias.
* **Fix**: Ensure `app.service-b.url` in `application.properties` is set to `http://winwin-data:8081/api/transform`.

### 2. "403 Forbidden" on Data API
* **Symptom**: The process endpoint returns an error indicating access is denied by the Data API.
* **Cause**: The `X-Internal-Token` header sent by Auth API does not match the one expected by Data API.
* **Fix**: Ensure both `application.properties` files have the exact same `app.internal.token` / `app.service-b.token` value.

### 3. "401 Unauthorized" on Process Endpoint
* **Symptom**: You cannot access `/api/process`.
* **Cause**: Missing or invalid JWT.
* **Fix**: Ensure you are sending the `Authorization: Bearer <token>` header in Postman. You must Login first to get this token.

### 4. Database Connection Failure
* **Symptom**: Auth API crashes immediately on startup.
* **Cause**: The API tried to start before the Postgres container was fully ready to accept connections.
* **Fix**: The `depends_on` condition in `docker-compose.yml` usually handles this. If it fails, simply restart the auth container: `docker compose restart winwin-auth`.


