# Team Tactic

A REST API backend for a task tracking and team collaboration application. Built with Spring Boot, JWT authentication, and a clean layered architecture following SOLID principles, OOP best practices, and domain-driven design.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### User Authentication & Management

- **User registration** – Create an account with email, password, and display name
- **Secure login** – JWT-based authentication with configurable token expiry
- **Profile management** – View and update your display name
- **Password security** – BCrypt hashing; passwords never stored or returned in plain text

### Task Management

- **Full CRUD** – Create, read, update, and delete tasks
- **Task attributes** – Title, description, status (OPEN, IN_PROGRESS, COMPLETED), due date
- **Task assignment** – Assign tasks to team members or unassign
- **Filtering & search** – Filter by status, assignee, team; search by title or description (case-insensitive)
- **Sorting & pagination** – Sort by allowlisted fields (`createdAt`, `updatedAt`, `dueDate`, `title`, `status`), with pagination support

### Team Collaboration

- **Teams** – Create teams with name and description; creator becomes OWNER
- **Membership** – Add members to teams; list team members; remove members (OWNER only)
- **Team-scoped tasks** – Tasks can belong to a team; only team members can view/assign
- **Roles** – OWNER and MEMBER; OWNER can remove members and delete comments/attachments

### Comments & Attachments

- **Comments** – Add, list, and delete comments on tasks; author or team OWNER can delete
- **Attachments** – Upload files (max 10 MB); list, download, and delete; stored on filesystem or configurable path

### Real-Time Notifications (SSE)

- **Server-Sent Events** – Subscribe to receive live notifications
- **Task assigned** – Notified when a task is assigned to you
- **Task updated** – Notified when a task you are assigned to is updated
- **Stable DTO payload** – Notifications send dedicated DTOs, not raw domain models

### Error Handling & Validation

- **Global exception handler** – Consistent error responses with `error` and `code` fields
- **Bean Validation** – All input DTOs validated; clear error messages for invalid fields
- **HTTP status codes** – 400, 401, 403, 404, 409, 413, 422 as appropriate
- **Production JWT guard** – Startup fails fast in `prod` if JWT secret is missing/weak/blocked or token expiry is invalid

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.3 |
| **Security** | Spring Security + JWT (jjwt 0.12) |
| **Data** | Spring Data JPA |
| **Database** | H2 (dev) / PostgreSQL (prod) |
| **Build** | Maven |
| **Validation** | Bean Validation (Jakarta) |

---

## Prerequisites

- **JDK 21** or higher
- **Maven 3.8+**
- **PostgreSQL** (optional, for production profile)

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/Team_tactic.git
cd Team_tactic
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

**Default (H2 file database – no extra setup):**

```bash
mvn spring-boot:run
```

- API base URL: `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console`  
  - JDBC URL: `jdbc:h2:file:./data/team_tactic`  
  - Username: `sa`  
  - Password: (leave empty)

**In-memory H2 (data cleared on restart):**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**PostgreSQL (production profile):**

```bash
# Set environment variables before running
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=team_tactic
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-min-32-chars-for-hs256

mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

On Windows (PowerShell):

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="team_tactic"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your-secret-min-32-chars-for-hs256"
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

### 4. Verify the Setup

```bash
curl http://localhost:8080/api/v1/health
```

Expected: `{"status":"UP"}`

---

## Configuration

| Property / Environment Variable | Default | Description |
|-------------------------------|--------|-------------|
| `server.port` / `SERVER_PORT` | 8080 | Server port |
| `app.jwt.secret` / `JWT_SECRET` | (dev key in yml) | JWT signing key (min 32 chars for HS256) |
| `app.jwt.expiration-ms` / `JWT_EXPIRATION_MS` | 86400000 (24h) | Token validity in milliseconds |
| `app.storage.root` / `STORAGE_ROOT` | ./uploads | Root directory for file attachments |
| `spring.profiles.active` | (none) | `prod` for PostgreSQL, `dev` for in-memory H2 |

**Production:** Set `JWT_SECRET` and `DB_PASSWORD` (and other `DB_*` vars) via environment. Do not commit secrets. Use `application-local.yml` (gitignored) for local overrides.

### Production JWT hardening

When `prod` profile is active, startup validation enforces:

- `app.jwt.secret` must be present and non-blank
- secret length must be at least 32 characters
- blocked insecure secrets are rejected (e.g. `changeme`, `secret`, dev default)
- `app.jwt.expiration-ms` must be positive

If any check fails, the application exits at startup with a clear error.

---

## API Reference

All endpoints except `/api/v1/auth/**` and `/api/v1/health` require a valid JWT in the `Authorization: Bearer <token>` header.

### Health

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/health` | Liveness check (returns `{"status":"UP"}`) |

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register – body: `{ "email", "password", "displayName" }` |
| POST | `/api/v1/auth/login` | Login – body: `{ "email", "password" }` → returns `{ "token", "expiresAt", ... }` |

### User Profile

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users/me` | Get current user profile |
| PATCH | `/api/v1/users/me` | Update profile – body: `{ "displayName" }` |

### Tasks

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tasks` | Create (body: `title`, `description`, `dueDate`, optional `teamId`) |
| GET | `/api/v1/tasks` | List – query: `teamId`, `assigneeId`, `status`, `q`, `page`, `size`, `sort`, `desc` |
| GET | `/api/v1/tasks/{id}` | Get one task |
| PATCH | `/api/v1/tasks/{id}` | Update (body: `title`, `description`, `status`, `dueDate` – all optional) |
| DELETE | `/api/v1/tasks/{id}` | Delete task |
| POST | `/api/v1/tasks/{id}/assign` | Assign – body: `{ "assigneeId": 2 }` or `{ "assigneeId": null }` to unassign |

### Tasks – Comments

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tasks/{id}/comments` | Add comment – body: `{ "body": "..." }` |
| GET | `/api/v1/tasks/{id}/comments` | List comments |
| DELETE | `/api/v1/tasks/{id}/comments/{commentId}` | Delete comment |

### Tasks – Attachments

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tasks/{id}/attachments` | Upload file – multipart: `file` |
| GET | `/api/v1/tasks/{id}/attachments` | List attachments |
| GET | `/api/v1/attachments/{id}` | Download attachment |
| DELETE | `/api/v1/attachments/{id}` | Delete attachment |

### Teams

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/teams` | Create – body: `{ "name", "description" }` |
| GET | `/api/v1/teams` | List teams where current user is member |
| GET | `/api/v1/teams/{id}` | Get team |
| GET | `/api/v1/teams/{id}/members` | List members |
| POST | `/api/v1/teams/{id}/members` | Add member – body: `{ "userId" }` |
| DELETE | `/api/v1/teams/{id}/members/{memberUserId}` | Remove member (OWNER only) |

### Notifications (SSE)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/notifications/subscribe` | Subscribe to SSE stream (returns `text/event-stream`) |

Events: `TASK_ASSIGNED`, `TASK_UPDATED` – payload format:

```json
{
  "type": "TASK_ASSIGNED",
  "timestamp": "2026-03-01T12:34:56.789Z",
  "task": {
    "id": 1,
    "title": "Task title",
    "description": "Task description",
    "status": "OPEN",
    "dueDate": "2026-03-15T12:00:00Z",
    "teamId": 10,
    "assigneeId": 2,
    "createdById": 1,
    "createdAt": "2026-03-01T10:00:00Z",
    "updatedAt": "2026-03-01T12:34:56.789Z"
  }
}
```

### API behavior notes

- `GET /api/v1/tasks?sort=...` accepts only: `createdAt`, `updatedAt`, `dueDate`, `title`, `status`
- invalid `sort` value returns `400 BAD_REQUEST`

---

## Project Structure

```
src/main/java/org/example/team_tactic/
├── api/                          # HTTP layer
│   ├── controller/               # REST controllers
│   ├── dto/                      # Request/response DTOs
│   └── exception/                # GlobalExceptionHandler
├── application/                  # Application layer (use cases)
│   ├── port/                     # Interfaces (repositories, services)
│   └── service/                  # Application services
├── domain/                       # Domain models (Task, Team, User, etc.)
└── infrastructure/               # Adapters
    ├── config/                   # SecurityConfig
    ├── notification/             # SSE notification service
    ├── persistence/              # JPA entities, repository implementations
    ├── security/                 # JWT filter, token provider
    └── storage/                  # File storage (attachments)
```

**Architecture:** Layered / ports & adapters – domain and application layers are independent of frameworks; infrastructure implements ports (JPA, JWT, file storage).

---

## Contributing

We welcome contributions. Please follow these guidelines:

### 1. Fork the Repository

Fork the repository and clone your fork locally.

### 2. Create a Branch

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

Use conventional branch names: `feature/`, `fix/`, `docs/`, `refactor/`.

### 3. Make Changes

- Follow existing code style and conventions
- Apply SOLID, KISS, DRY, YAGNI principles
- Add or update tests as needed
- Keep commits focused and atomic

### 4. Run Tests

```bash
mvn test
```

### Current unit-test baseline

The project currently includes unit tests for key business services:

- `AssignTaskServiceTest`
- `UpdateTaskServiceTest`
- `DeleteCommentServiceTest`

Run only these tests:

```bash
mvn -Dtest=AssignTaskServiceTest,UpdateTaskServiceTest,DeleteCommentServiceTest test
```

### 5. Commit

Use clear commit messages:

```
feat: add task filtering by status
fix: handle null assignee in notification
docs: update API reference
```

### 6. Push and Open a Pull Request

```bash
git push origin feature/your-feature-name
```

Then open a Pull Request on GitHub with:

- A clear title and description
- Reference to any related issues
- Summary of changes

### 7. Code Review

Address feedback from reviewers. Once approved, your PR can be merged.

---

## License

Use as needed for your project.