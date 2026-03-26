Warhammer 40k Roster Builder API 🚀

REST API for creating, managing Warhammer 40k army rosters.

✨ Features

    🪖 Faction management (Space Marines, Orks, Necrons, etc.)

    ⚔️ Roster creation with detachments and units

    🎒 Wargear and upgrades configuration with automatic points calculation

    ✅ Roster validation against tournament rules (CP, power level)

    👥 User system with roles (player, organizer)

    📊 PDF/JSON roster export

🛠 Tech Stack

    Backend: Spring Boot 3.x, Spring Data JPA, Spring Security (JWT)
    Database: PostgreSQL
    Documentation: OpenAPI (Swagger)
    Tests: JUnit 5, Testcontainers
    Docker: Compose (app + postgres + pgadmin)
    CI/CD: GitHub Actions

🚀 Quick Start

    Prerequisites:
    
        Java 21
    
        Docker (optional)
    
        PostgreSQL 15+

Clone & Build

    git clone https://github.com/pl-sitnik/roster-builder-api.git
    cd roster-builder-api
    mvn clean install

Run with Docker

    docker-compose up -d
    mvn spring-boot:run

📡 API Endpoints

Swagger UI: http://localhost:8080/swagger-ui.html

    POST /api/v1/factions       # Create faction
    POST /api/v1/rosters        # Create roster
    GET  /api/v1/rosters/{id}   # Get roster
    PUT  /api/v1/rosters/{id}   # Update roster
    POST /api/v1/auth/login     # JWT login

🗄 Database Schema

    User → Roster → Detachment → Unit → Equipment
    ↓
    Faction
See /docs/architecture.md for details

🧪 Testing

    mvn test                    # All tests
    mvn test -Dtest=*RosterServiceTest  # Single class
Coverage: 85%+

🔧 Configuration

# application.yml
spring:
    datasource:
        url: jdbc:postgresql://localhost:5432/rosterdb
    jpa:
        hibernate:
            ddl-auto: validate

Full config in application-local.yml

📁 Project Structure

    src/main/java/pl/sitnik/warhammer/rosterbuilderapi/
    ├── config/          # Spring Security, JPA, Swagger
    ├── controller/      # REST endpoints
    ├── dto/             # Request/Response DTOs
    ├── entity/          # JPA Entities
    ├── exception/       # Global exception handling
    ├── repository/      # Spring Data JPA
    ├── security/        # JWT, Authentication
    ├── service/         # Business logic
    └── util/            # Helpers, mappers