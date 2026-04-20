# Zion Center — Backend API

Spring Boot 4.0 REST API for the Zion Center online Bible classes platform.
This file is the reference for the Next.js frontend when building API integrations.

---

## Stack

| Layer | Technology |
| --- | --- |
| Runtime | Java 21 |
| Framework | Spring Boot 4.0 |
| Security | Spring Security 6 + JWT (JJWT 0.12.6) |
| Database | MySQL + Hibernate (JPA) |
| Migrations | Flyway 11 |
| Build | Maven |

---

## Running Locally

```bash
./mvnw spring-boot:run
```

Starts on `http://localhost:8080`. Active profile: `dev` (default).

Required: MySQL on `localhost:3306` with database `revealed_word_store`.
All dev credentials have fallback defaults in `application-dev.yaml` — no `.env` needed locally.

---

## Authentication Flow

This API uses JWT access tokens (15 min) + opaque refresh tokens (7 days).

### Login

```
POST /api/auth/login
```

**Request:**

```json
{
  "email": "user@example.com",
  "password": "Password1!"
}
```

**Response `200`:**

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<opaque-hex-token>",
  "name": "John Doe",
  "email": "user@example.com",
  "role": "ADMIN"
}
```

`role` is either `ADMIN` or `TEACHER`.

---

### Using the Access Token

Every protected request must include:

```
Authorization: Bearer <accessToken>
```

---

### Refresh Access Token

```
POST /api/auth/refresh
```

**Request:**

```json
{
  "refreshToken": "<current-refresh-token>"
}
```

**Response `200`:**

```json
{
  "accessToken": "<new-jwt>",
  "refreshToken": "<new-refresh-token>"
}
```

Refresh tokens rotate on every use — always store the latest one returned.
The previous token is invalidated immediately.

---

### Logout

```
POST /api/auth/logout
```

**Request:**

```json
{
  "refreshToken": "<refresh-token>"
}
```

**Response:** `204 No Content`

---

### Suggested Next.js Token Management

- Store `accessToken` in memory (React state / Zustand)
- Store `refreshToken` in an `httpOnly` cookie
- On `401` response → call `/api/auth/refresh` → retry original request
- On refresh failure → redirect to `/login`

---

## Authorization Roles

| Role | Access |
| --- | --- |
| `ADMIN` | Full access to all endpoints |
| `TEACHER` | Access to own classes, lessons, links — cannot manage users |

---

## Error Response Format

All errors return:

```json
{
  "error": "Human-readable message"
}
```

| Status | Meaning |
| --- | --- |
| `400` | Validation failure or bad request |
| `401` | Missing or expired access token |
| `403` | Authenticated but not authorized |
| `404` | Resource not found or unknown route |
| `405` | Wrong HTTP method |
| `409` | Duplicate record (unique constraint) |
| `429` | Rate limit exceeded on login |
| `500` | Unexpected server error |

Rate-limited responses also include headers:

```
Retry-After: <seconds>
X-RateLimit-Remaining: <count>
```

---

## API Reference

Base URL: `http://localhost:8080` (dev) / `https://YOUR_DOMAIN` (prod)

---

### Auth — `/api/auth` — Public

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/auth/login` | Login, returns both tokens |
| POST | `/api/auth/refresh` | Rotate refresh token, get new access token |
| POST | `/api/auth/logout` | Invalidate refresh token |

---

### Users — `/api/users` — ADMIN only (except change-password)

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/users/me` | Get currently authenticated user (any role) |
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |
| POST | `/api/users/{id}/change-password` | Change own password (authenticated user) |

**Create user — request:**

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password1!",
  "role": "TEACHER"
}
```

**Update user — request:**

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "role": "ADMIN",
  "active": true
}
```

**Change password — request:**

```json
{
  "oldPassword": "OldPassword1!",
  "newPassword": "NewPassword2@"
}
```

**User response:**

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "ADMIN",
  "active": true,
  "createdAt": "2026-04-17T08:00:00"
}
```

**Password policy** (enforced on create and change-password):

- Minimum 8 characters, maximum 128
- At least one uppercase letter (A-Z)
- At least one lowercase letter (a-z)
- At least one digit (0-9)
- At least one special character (`!@#$%^&*` etc.)

---

### Categories — `/api/categories` — Authenticated

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/categories` | List all categories |
| POST | `/api/categories` | Create category |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category (ADMIN only) |

**Request:**

```json
{
  "name": "New Testament",
  "description": "Classes on the New Testament"
}
```

**Response:**

```json
{
  "id": 1,
  "name": "New Testament",
  "description": "Classes on the New Testament",
  "createdAt": "2026-04-17T08:00:00"
}
```

---

### Classes — `/api/classes` — Authenticated

Teachers only see their own classes. Admins see all.

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/classes` | List classes (scoped by role) |
| GET | `/api/classes/{id}` | Get class by ID |
| POST | `/api/classes` | Create class |
| PUT | `/api/classes/{id}` | Update class |
| DELETE | `/api/classes/{id}` | Delete class |

**Request:**

```json
{
  "title": "The Gospel of John",
  "description": "A deep study of John's Gospel",
  "classDate": "2026-04-20"
}
```

**Response:**

```json
{
  "id": 1,
  "title": "The Gospel of John",
  "description": "A deep study of John's Gospel",
  "classDate": "2026-04-20",
  "status": "active",
  "createdAt": "2026-04-17T08:00:00"
}
```

`status` values: `draft`, `active`, `archived`

---

### Lessons — `/api/lessons` — Authenticated

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/lessons/class/{classId}` | Get all lessons in a class |
| GET | `/api/lessons/category/{categoryId}` | Get all lessons in a category |
| GET | `/api/lessons/{id}` | Get lesson by ID |
| POST | `/api/lessons` | Create lesson |
| PUT | `/api/lessons/{id}` | Update lesson |
| DELETE | `/api/lessons/{id}` | Delete lesson |

**Request:**

```json
{
  "classId": 1,
  "title": "Introduction",
  "description": "Overview of John chapter 1",
  "youtubeUrl": "https://www.youtube.com/watch?v=XXXXXXXXXXX",
  "durationSeconds": 3600,
  "categoryId": 1
}
```

`categoryId` is required.

**Response:**

```json
{
  "id": 1,
  "classId": 1,
  "classTitle": "The Gospel of John",
  "categoryId": 1,
  "categoryName": "New Testament",
  "title": "Introduction",
  "description": "Overview of John chapter 1",
  "youtubeUrl": "https://www.youtube.com/watch?v=XXXXXXXXXXX",
  "youtubeVideoId": "XXXXXXXXXXX",
  "durationSeconds": 3600,
  "isActive": true,
  "createdAt": "2026-04-17T08:00:00"
}
```

---

### Shared Links — `/api/links` — Authenticated

Generate time-limited or view-limited shareable links to lessons for public viewers.

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/links` | Generate a shared link |
| GET | `/api/links` | List own shared links |
| PATCH | `/api/links/{id}/activate` | Re-activate a link |
| DELETE | `/api/links/{id}` | Deactivate a link |

**Request:**

```json
{
  "lessonId": 1,
  "expiresAt": "2026-05-01T00:00:00",
  "maxViews": 100
}
```

`expiresAt` and `maxViews` are optional. Omit for unlimited.

**Response:**

```json
{
  "id": 1,
  "token": "abc123xyz",
  "watchUrl": "http://localhost:8080/api/watch/abc123xyz",
  "lessonId": 1,
  "lessonTitle": "Introduction",
  "expiresAt": "2026-05-01T00:00:00",
  "maxViews": 100,
  "viewCount": 0,
  "isActive": true,
  "createdAt": "2026-04-17T08:00:00"
}
```

---

### Watch — `/api/watch` — Public

Used by the public-facing video player page. No token required.

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/watch/{token}` | Resolve token, return video info |

**Response `200`:**

```json
{
  "lessonTitle": "Introduction",
  "classTitle": "The Gospel of John",
  "youtubeUrl": "https://www.youtube.com/watch?v=XXXXXXXXXXX",
  "youtubeVideoId": "XXXXXXXXXXX",
  "description": "Overview of John chapter 1"
}
```

Returns `403` if the link is inactive, expired, or has exceeded max views.

---

### Health — Public

| Method | Path | Description |
| --- | --- | --- |
| GET | `/actuator/health` | Returns `{"status":"UP"}` |

---

## Environment Variables (Production)

| Variable | Description |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | Must be `prod` |
| `DB_URL` | `jdbc:mysql://host:3306/revealed_word_store` |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | 256-bit hex string for signing JWT |
| `JWT_EXPIRATION_MS` | Access token TTL in ms — default `900000` (15 min) |
| `JWT_REFRESH_EXPIRATION_MS` | Refresh token TTL in ms — default `604800000` (7 days) |
| `APP_BASE_URL` | e.g. `https://api.zioncenter.com` |
| `CORS_ALLOWED_ORIGINS` | Frontend URL(s), comma-separated |
| `SERVER_PORT` | Default `8080` |
| `MANAGEMENT_PORT` | Actuator port, default `8081` |
| `RATE_LIMIT_LOGIN_CAPACITY` | Max login attempts per window — default `5` |
| `RATE_LIMIT_LOGIN_REFILL_SECONDS` | Window size in seconds — default `60` |

---

## Project Structure

```
src/main/java/com/zion/zion_center/
├── config/          # JPA auditing config
├── controller/      # REST controllers
├── dto/             # Request and Response records per domain
├── entity/          # JPA entities (User, Class, Lesson, SharedLink, RefreshToken)
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── mapper/          # MapStruct mappers (entity to DTO)
├── repository/      # Spring Data JPA repositories
├── security/        # JwtUtil, JwtAuthFilter, RateLimitFilter, SecurityConfig
├── service/         # Business logic
└── validation/      # @ValidPassword annotation + validator

src/main/resources/
├── application.yaml              # Profile selector only
├── application-dev.yaml          # Dev config (with fallback defaults)
├── application-prod.yaml         # Prod config (env vars, no secret defaults)
└── db/migration/
    ├── V1__initial_schema.sql    # All original tables
    └── V2__refresh_tokens.sql    # Refresh token table

nginx/
└── nginx.conf                    # SSL termination + reverse proxy config
```
