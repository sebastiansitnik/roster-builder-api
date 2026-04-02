Warhammer 40k Roster Builder API - WORK IN PROGRESS🚀

[![Java CI](https://github.com/sebastiansitnik/roster-builder-api/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/sebastiansitnik/roster-builder-apiactions/workflows/ci.yml)

REST API for creating, managing Warhammer 40k army rosters.

✨ Features

    🪖 Faction management (Space Marines, Orks, Necrons, etc.)

    ⚔️ Simple Roster creation

🛠 Tech Stack

    Backend: Spring Boot 3.3.13, Spring Data JPA, Spring Security (JWT)
    Database: H2
    Documentation: OpenAPI (Swagger)
    Tests: JUnit 5

🚀 Quick Start

    Prerequisites:
    
        Java 21

Clone & Build

    git clone https://github.com/sebastiansitnik/roster-builder-api
    cd roster-builder-api
    mvn clean install

📡 API Endpoints

Swagger UI: http://localhost:8080/swagger-ui.html

🗄 Database Schema

    Faction -> Roster

See /docs/architecture.md for details

🧪 Testing

    mvn test                    # All tests
    mvn test -Dtest=*RosterServiceTest  # Single class
Coverage: 100%

🔧 Configuration

Full config in application.yml

📁 Project Structure

    src/main/java/com/sitnik/warhammer/rosterbuilderapi/
    ├── config/          # Spring Security, JPA, Swagger
    ├── controller/      # REST endpoints
    ├── dto/             # Request/Response DTOs
    ├── entity/          # JPA Entities
    ├── enums/           # Domain enums
    ├── exception/       # Global exception handling
    ├── repository/      # Spring Data JPA
    ├── security/        # JWT, Authentication
    ├── service/         # Business logic
    └── util/            # Helpers, mappers