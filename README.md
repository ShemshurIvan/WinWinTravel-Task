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