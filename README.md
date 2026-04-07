# Warhammer 40k Roster Builder API 🚀

REST API for building and managing Warhammer 40k army rosters. Features faction management, roster creation, JWT authentication, Swagger docs, and full test coverage.

[![Java CI](https://github.com/sebastiansitnik/roster-builder-api/actions/workflows/ci.yml/badge.svg)](https://github.com/sebastiansitnik/roster-builder-api/actions/workflows/ci.yml)
[![Qodana](https://github.com/sebastiansitnik/roster-builder-api/actions/workflows/qodana_code_quality.yml/badge.svg?branch=main)](https://github.com/sebastiansitnik/roster-builder-api/actions/workflows/qodana_code_quality.yml)
## ✨ Key Features

- 🪖 **Faction Management** - Space Marines, Orks, Necrons, etc.
- ⚔️ **Roster Building** - Create and validate army lists
- 🔐 **JWT Authentication** - Secure API access
- 📊 **Swagger UI** - Interactive API docs
- ✅ **100% Test Coverage** - Unit & integration tests

## 🛠 Tech Stack

| Backend | Database | Security | Docs | Testing |
|---------|----------|----------|------|---------|
| Spring Boot 3.3.13 | H2 | Spring Security + JWT | OpenAPI/Swagger | JUnit 5 |

## 🚀 Quick Start

### Prerequisites
- **Java 21+**
- **Maven 3.9+**

```bash
# Clone & build
git clone https://github.com/sebastiansitnik/roster-builder-api
cd roster-builder-api
mvn clean install

# Run
mvn spring-boot:run

# API Docs
http://localhost:8080/swagger-ui.html
```

## 📡 API Example

```bash
# Get factions
curl -X GET http://localhost:8080/api/v1/factions

# Create roster (with JWT)
curl -X POST http://localhost:8080/api/v1/rosters \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"factionId": 1, "units": [...]}'
```

## 🧪 Testing

```bash
mvn test                    # All tests (100% coverage)
mvn test -Dtest=*ControllerTest  # Controller tests only
```

## 🗄 Project Structure

src/main/java/com/sitnik/warhammer/rosterbuilderapi/

    ├── config/ # Security, JPA, Swagger
    ├── controller/ # REST endpoints
    ├── dto/ # Request/Response DTOs
    ├── entity/ # JPA entities
    ├── repository/ # Spring Data JPA
    ├── security/ # JWT auth
    ├── service/ # Business logic
    └── exception/ # Global error handling

---

**Built with Spring Boot** | **100% Test Coverage** | **Swagger Documented** | **JWT Secured**