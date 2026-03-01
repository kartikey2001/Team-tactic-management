# Team Tactic

Task tracking and team collaboration backend API. Built with Spring Boot, JWT authentication, and a clean layered architecture (domain, application, infrastructure, API).

## Tech stack

- **Java 21**
- **Spring Boot 3.3**
- **Spring Security** + JWT
- **Spring Data JPA**
- **H2** (development) / **PostgreSQL** (production)
- **Maven**

## Prerequisites

- JDK 21
- Maven 3.8+
- (Optional) PostgreSQL for production

## Run locally

**Default (H2 file DB, no extra setup):**

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/team_tactic`)

**In-memory H2 (data cleared on restart):**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**PostgreSQL (production profile):**

```bash
# Set env vars: DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD, JWT_SECRET
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## API overview

All endpoints except auth and health require a valid JWT in the `Authorization: Bearer <token>` header.

| Area        | Base path           | Description                    |
|------------|---------------------|--------------------------------|
| Health     | `GET /actuator/health` | Liveness/readiness             |
| Auth       | `/api/v1/auth/**`   | Register, login (returns JWT)  |
| User       | `/api/v1/users/me`  | Get/update current user profile|
| Tasks      | `/api/v1/tasks`     | CRUD, assign, filter, search   |
| Teams      | `/api/v1/teams`     | Create/list teams, members     |

### Auth

- `POST /api/v1/auth/register` – body: `{ "email", "password", "displayName" }`
- `POST /api/v1/auth/login` – body: `{ "email", "password" }` → returns `{ "token", "expiresAt" }`

### Tasks

- `POST /api/v1/tasks` – create (optional `teamId`; caller must be team member)
- `GET /api/v1/tasks` – list (query: `teamId`, `assigneeId`, `status`, `q`, `page`, `size`, `sort`, `desc`)
- `GET /api/v1/tasks/{id}` – get one (team tasks require membership)
- `PATCH /api/v1/tasks/{id}` – update
- `DELETE /api/v1/tasks/{id}` – delete
- `POST /api/v1/tasks/{id}/assign` – body: `{ "assigneeId" }` (or `null` to unassign)

### Teams

- `POST /api/v1/teams` – create (body: `name`, `description`); creator becomes OWNER
- `GET /api/v1/teams` – list teams where current user is a member
- `GET /api/v1/teams/{id}` – get team (caller must be member)
- `GET /api/v1/teams/{id}/members` – list members
- `POST /api/v1/teams/{id}/members` – add member (body: `{ "userId" }`)

## Configuration

| Property / env           | Default (dev)              | Description                |
|--------------------------|----------------------------|----------------------------|
| `server.port` / `SERVER_PORT` | 8080                    | Server port                |
| `app.jwt.secret` / `JWT_SECRET` | (dev key in yml)     | Min 32 chars for HS256     |
| `app.jwt.expiration-ms`  | 86400000 (24h)             | Token validity             |
| `spring.profiles.active` | (none)                     | `prod` for PostgreSQL      |

For production, set `JWT_SECRET` and `DB_PASSWORD` (and other `DB_*` vars when using `prod` profile). Do not commit secrets; use env or a local config file (e.g. `application-local.yml`, already in `.gitignore`).

## Project structure

```
src/main/java/org/example/team_tactic/
├── api/                    # HTTP layer
│   ├── controller/          # REST controllers
│   ├── dto/                # Request/response DTOs
│   └── exception/           # GlobalExceptionHandler
├── application/             # Application layer
│   ├── port/               # Repository interfaces
│   └── service/            # Use-case services
├── domain/                  # Domain models (Task, Team, User, etc.)
└── infrastructure/          # Adapters
    ├── config/             # SecurityConfig, etc.
    ├── persistence/        # JPA entities, repository implementations
    └── security/            # JWT filter, token provider
```

## Build

```bash
mvn clean compile
mvn clean package
```

## License

Use as needed for your project.
