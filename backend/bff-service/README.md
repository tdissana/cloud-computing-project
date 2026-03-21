# BFF Service — watupa.lk

Backend-for-Frontend gateway for the TechSalary LK platform.
All frontend API calls go through here — no microservice is directly exposed.

---

## Architecture Role

```
Browser (Next.js)
    │
    │  HTTP  /bff/api/*
    ▼
BFF Service :8080          ← YOU ARE HERE
    ├─ /api/auth/*    →  identity-service        :8081
    ├─ /api/submissions → salary-submission-service :8082
    ├─ /api/search    →  search-service          :8083
    ├─ /api/stats     →  stats-service           :8084
    └─ /api/votes     →  vote-service            :8085  (JWT required)
```

---

## Route Map

| Method | BFF Path                | Auth? | Forwards To                              |
|--------|-------------------------|-------|------------------------------------------|
| POST   | `/bff/api/auth/signup`  | ❌     | `identity-service /api/auth/signup`      |
| POST   | `/bff/api/auth/login`   | ❌     | `identity-service /api/auth/login`       |
| POST   | `/bff/api/submissions`  | ❌     | `salary-submission-service /api/submissions` |
| POST   | `/bff/api/search/salaries` | ❌  | `search-service /api/search/salaries`    |
| GET    | `/bff/api/search/filters`  | ❌  | `search-service /api/search/filters`     |
| GET    | `/bff/api/search`          | ❌  | `search-service /api/search` (legacy)    |
| GET    | `/bff/api/stats`        | ❌     | `stats-service /api/stats`               |
| POST   | `/bff/api/votes`        | ✅ JWT | `vote-service /api/votes`                |
| POST   | `/bff/api/reports`      | ✅ JWT | `vote-service /api/reports`              |
| GET    | `/bff/info`             | ❌     | BFF info (local)                         |
| GET    | `/actuator/health`      | ❌     | Spring Actuator (K8s probes)             |

---

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker (for containerised run)
- Identity service running on `localhost:8081`

---

## Run Locally (identity-service already on :8081)

```bash
# 1. Clone / navigate to bff-service directory
cd bff-service

# 2. Run with Maven (uses application.yml defaults)
./mvnw spring-boot:run

# BFF starts on http://localhost:8080
```

### Override service URLs via env vars:

```bash
IDENTITY_SERVICE_URL=http://localhost:8081 \
CORS_ALLOWED_ORIGINS=http://localhost:3000 \
./mvnw spring-boot:run
```

---

## Run with Docker

```bash
# Build image
docker build -t bff-service:local .

# Run (identity-service must be reachable from the container)
docker run -p 8080:8080 \
  -e IDENTITY_SERVICE_URL=http://host.docker.internal:8081 \
  -e CORS_ALLOWED_ORIGINS=http://localhost:3000 \
  bff-service:local
```

---

## Run with Docker Compose (full local stack)

```bash
# First build your identity-service image:
cd ../identity-service
docker build -t identity-service:local .

# Then start everything:
cd ../bff-service
docker compose up --build
```

---

## Test the Endpoints

### Signup
```bash
curl -X POST http://localhost:8080/bff/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"pass123"}'
```

Expected:
```json
{
  "success": true,
  "data": { "message": "User registered successfully" },
  "timestamp": "2026-..."
}
```

### Login
```bash
curl -X POST http://localhost:8080/bff/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"pass123"}'
```

Expected:
```json
{
  "success": true,
  "data": { "message": "Login successful", "token": "eyJ..." },
  "timestamp": "2026-..."
}
```

### Vote (protected — use token from login)
```bash
TOKEN="eyJ..."

curl -X POST http://localhost:8080/bff/api/votes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"submissionId":"uuid-here","voteType":"UP"}'
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/bff/info
```

---

## CORS Configuration

Allowed origins are controlled by the `CORS_ALLOWED_ORIGINS` environment variable:

```bash
# Local dev
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Production (AKS)
CORS_ALLOWED_ORIGINS=https://techsalary.com,https://www.techsalary.com
```

The `CorsFilter` bean (not `WebMvcConfigurer`) is used so that CORS headers
are present even on error responses (401, 400, 502), allowing the browser
to read error messages from failed requests.

---

## Deploy to AKS

```bash
# 1. Build and push to Azure Container Registry
az acr build \
  --registry <ACR_NAME> \
  --image bff-service:latest \
  .

# 2. Update the image name in k8s-bff.yaml, then apply
kubectl apply -f k8s-bff.yaml

# 3. Verify
kubectl get pods -n app
kubectl logs -n app deploy/bff-service
```

---

## Project Structure

```
bff-service/
├── src/main/java/lk/watupa/bff/
│   ├── BffServiceApplication.java        # Entry point
│   ├── config/
│   │   ├── CorsProperties.java           # Binds cors.allowed-origins
│   │   ├── ServiceProperties.java        # Binds all downstream URLs
│   │   ├── RestTemplateConfig.java       # HTTP client with timeouts
│   │   └── WebConfig.java               # CorsFilter bean
│   ├── controller/
│   │   ├── AuthController.java           # /bff/api/auth/* (public)
│   │   ├── SalaryController.java         # /bff/api/submissions|search|stats
│   │   ├── VoteController.java           # /bff/api/votes|reports (JWT)
│   │   └── HealthController.java         # /bff/info
│   ├── dto/
│   │   ├── ApiResponse.java              # Uniform response envelope
│   │   └── AuthDto.java                  # Signup/Login request+response
│   ├── exception/
│   │   ├── DownstreamException.java      # Carries HTTP status from downstream
│   │   └── GlobalExceptionHandler.java   # Maps all exceptions → ApiResponse
│   ├── filter/
│   │   └── RequestLoggingFilter.java     # Logs method/URI/status/duration
│   └── service/
│       ├── AuthTokenService.java         # Calls identity /validate
│       └── ProxyService.java            # Generic HTTP forwarding helper
├── src/main/resources/
│   └── application.yml
├── Dockerfile
├── docker-compose.yml
└── k8s-bff.yaml
```
