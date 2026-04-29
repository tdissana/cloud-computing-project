# Watupa — TechSalary LK Platform

A community-driven salary transparency platform for the Sri Lankan tech industry, built as a cloud-native microservices application deployed on Kubernetes.

---

## Overview

Watupa allows tech professionals to anonymously submit and explore salary data, vote on submissions for credibility, and view aggregated statistics. The system is designed for scalability and separation of concerns, with each capability encapsulated in its own microservice.

---

## Architecture

```
Browser (Next.js Frontend)
        │
        │  HTTP
        ▼
  BFF Service :8000          ← Single entry point for all frontend calls
        ├─ /api/auth/*    →  Identity Service         :8001
        ├─ /api/submissions → Salary Submission Svc   :8002
        ├─ /api/search    →  Search Service           :8003
        ├─ /api/stats     →  Stats Service            :8004
        └─ /api/votes     →  Vote Service             :8005
                                        │
                                        ▼
                              PostgreSQL Database     :5432
```

All services are deployed in a Kubernetes cluster using two namespaces: `watupa-app` (application tier) and `watupa-data` (database tier).

---

## Services

### Frontend
- **Tech:** Next.js 16, React 19, TypeScript, Tailwind CSS, shadcn/ui, Recharts, MUI
- **Pages:** Home, Auth (login/signup), Salary Search, Salary Submission, Statistics Dashboard
- **Features:** JWT-based session management via cookies, password strength validation, searchable multi-select filters, salary card display with vote counts, interactive charts

### BFF Service (Backend for Frontend)
- **Tech:** Java 21, Spring Boot
- **Role:** API gateway that proxies all browser requests to the appropriate downstream microservice. No microservice is directly exposed to the client.
- **Features:** JWT validation for protected routes, request logging filter, CORS configuration, uniform error response envelope, health endpoint for Kubernetes probes

### Identity Service
- **Tech:** Java 21, Spring Boot, Spring Security, PostgreSQL (`identity` schema)
- **Features:** User registration and login, JWT token generation and validation, password hashing, unique username/email enforcement

### Salary Submission Service
- **Tech:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL (`salary` schema)
- **Features:** Anonymous salary submission (company, job title, experience level, employment type, seniority, skills, base salary, total compensation), submission status lifecycle (`PENDING` → approved/rejected), paginated query and filtering API, JPA Specification-based dynamic filtering

### Search Service
- **Tech:** Java 21, Spring Boot
- **Features:** Multi-criteria salary search (role, company, employment type, seniority, currency, experience level), sortable and paginated results, vote count enrichment (fetched from Vote Service), custom validation annotations for all filter fields, filter options endpoint for populating UI dropdowns

### Stats Service
- **Tech:** Java 21, Spring Boot
- **Features:** Aggregated salary statistics (min, max, average, median), fetches approved submissions from the Salary Submission Service and computes stats on-the-fly

### Vote Service
- **Tech:** Java 21, Spring Boot, PostgreSQL (`community` schema)
- **Features:** Authenticated upvote/downvote on salary submissions (one vote per user per submission), vote toggling and removal, denormalized vote count table for fast reads, submission status verification before allowing votes

### Database
- **Tech:** PostgreSQL 15, Docker/Kubernetes
- **Schemas:** `identity` (users), `salary` (submissions), `community` (votes, vote_counts)
- **Init:** Schema and tables bootstrapped via SQL init scripts on first startup

---

## Key Features

| Feature | Description |
|---|---|
| Anonymous Submissions | Users can submit salary data with optional anonymization |
| JWT Authentication | Secure login/signup with token-based sessions; protected endpoints require a valid JWT |
| Community Voting | Authenticated upvote/downvote system with one-vote-per-user enforcement |
| Salary Search | Multi-filter search across company, role, seniority, employment type, experience, and currency |
| Statistics Dashboard | Aggregated salary stats (min, max, average, median) with interactive Recharts visualizations |
| BFF Gateway | Single API entry point; no microservice is directly reachable from the browser |
| Kubernetes Deployment | Full K8s manifests with namespaces, ConfigMaps, Secrets, Deployments, and Services |
| CI/CD Pipelines | GitHub Actions workflows for backend (Maven build + test) and frontend (TypeScript check, ESLint, Next.js build) |
| Docker Support | Dockerfiles for all 7 services; images tagged as `watupa/<service>:latest` |

---

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | Next.js 16, React 19, TypeScript, Tailwind CSS v4, shadcn/ui, MUI, Recharts |
| Backend Services | Java 21, Spring Boot, Spring Security, Spring Data JPA |
| Database | PostgreSQL 15 |
| Container Runtime | Docker |
| Orchestration | Kubernetes (K3s / k3d for local) |
| CI/CD | GitHub Actions |
| Build Tools | Maven (backend), npm (frontend) |

---

## Project Structure

```
cloud-computing-project/
├── frontend/                        # Next.js application
│   └── src/
│       ├── app/                     # Pages: auth, search, submit, stats
│       ├── components/              # UI components per feature domain
│       ├── lib/                     # API clients and helpers
│       └── types/                   # TypeScript type definitions
├── backend/
│   ├── bff-service/                 # API gateway
│   ├── identity-service/            # Auth & user management
│   ├── salary-submission-service/   # Salary data CRUD
│   ├── search-service/              # Search & filter
│   ├── stats-service/               # Aggregated statistics
│   ├── vote-service/                # Community voting
│   └── pom.xml                      # Parent Maven POM
├── database/
│   └── postgres/
│       ├── docker-compose.yml
│       └── init/001_init.sql        # Schema bootstrap
├── k8/
│   ├── dockerfiles/                 # One Dockerfile per service
│   ├── manifests/                   # Kubernetes YAML manifests
│   └── scripts/                     # build.sh, deploy.sh, port-forward.sh
└── .github/workflows/
    ├── backend-ci.yml
    └── frontend-ci.yml
```

---

## Getting Started

### Prerequisites

- Docker Desktop
- kubectl
- k3d or K3s
- Java 21+, Maven 3.9+ (for local backend development)
- Node.js 20+, npm (for local frontend development)

### Deploy to Kubernetes (Local)

```bash
# 1. Create a local Kubernetes cluster
k3d cluster create watupa-cluster \
  --api-port 6443 \
  --port 80:80@loadbalancer \
  --port 443:443@loadbalancer

# 2. Build all Docker images
./k8/scripts/build.sh

# 3. Deploy all services
./k8/scripts/deploy.sh

# 4. Forward ports and access the app
./k8/scripts/port-forward.sh
# Open http://localhost:3000
```

### Run Frontend Locally

```bash
cd frontend
npm install
npm run dev
# Open http://localhost:3000
```

### Run a Backend Service Locally

```bash
cd backend/identity-service
./mvnw spring-boot:run
```

### Verify Deployment

```bash
kubectl get pods -n watupa-app
kubectl get pods -n watupa-data
kubectl get services -n watupa-app
```

---

## CI/CD

Two GitHub Actions workflows run on push/PR to `main` and `development`:

**Backend CI** (`backend-ci.yml`)
- Sets up Java 21 (Temurin)
- Caches Maven dependencies
- Builds all services and runs tests (`mvn clean install`)
- Uploads JAR artifacts

**Frontend CI** (`frontend-ci.yml`)
- Sets up Node.js 20 with npm cache
- Installs dependencies (`npm ci`)
- Runs TypeScript type checking
- Runs ESLint
- Builds the Next.js application

---

## Database Schema

| Schema | Tables | Purpose |
|---|---|---|
| `identity` | `users` | User accounts (username, email, hashed password) |
| `salary` | `submission` | Salary submissions with all compensation details |
| `community` | `votes`, `vote_counts` | Vote records and denormalized vote count aggregates |

---

## Service Ports Reference

| Service | Port |
|---|---|
| Frontend | 3000 |
| BFF Service | 8000 |
| Identity Service | 8001 |
| Salary Submission Service | 8002 |
| Search Service | 8003 |
| Stats Service | 8004 |
| Vote Service | 8005 |
| PostgreSQL | 5432 |

---

## License

This project was developed as part of a cloud computing coursework. See individual service directories for specific configuration and usage details.

---

# Watupa Cloud Computing Project - Kubernetes Deployment

This guide provides step-by-step instructions to deploy the Watupa multi-service application to a local Kubernetes cluster using K3s or k3d.

## Prerequisites

- Docker Desktop installed and running
- Git installed
- kubectl installed
- For macOS: Homebrew (for installing K3s/k3d)
- For Windows: Chocolatey or manual installation

## Architecture

The application consists of:
- **Frontend**: Next.js app (port 3000)
- **BFF Service**: Backend-for-Frontend (port 8000)
- **Identity Service**: User management (port 8001)
- **Salary Submission Service**: Salary data (port 8002)
- **Search Service**: Search functionality (port 8003)
- **Stats Service**: Statistics (port 8004)
- **Vote Service**: Voting system (port 8005)
- **Database**: PostgreSQL (port 5432)

All services communicate via Kubernetes services, preserving original ports and endpoints.

## Setup Instructions

### 1. Install Kubernetes Cluster

#### macOS (Intel/Apple Silicon)
```bash
# Using k3d (recommended for local dev)
brew install k3d
k3d cluster create watupa-cluster --api-port 6443 --port 80:80@loadbalancer --port 443:443@loadbalancer
```

#### Windows
```powershell
# Using k3d
choco install k3d
k3d cluster create watupa-cluster --api-port 6443 --port 80:80@loadbalancer --port 443:443@loadbalancer
```

Alternatively, use K3s:
```bash
# macOS
brew install k3s
sudo k3s server --docker
# Set KUBECONFIG
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml
```

### 2. Clone and Navigate to Project
```bash
git clone <your-repo>
cd cloud-computing-project
```

### 3. Build Docker Images
```bash
./k8/scripts/build.sh
```

This builds all service images with tags `watupa/<service>:latest`.

### 4. Deploy to Kubernetes
```bash
./k8/scripts/deploy.sh
```

This applies all manifests and waits for pods to be ready.

### 5. Access the Application

#### Option 1: Port Forwarding
```bash
./k8/scripts/port-forward.sh
```
Access at: http://localhost:3000

#### Option 2: Ingress (if using K3s with Traefik)
Add to `/etc/hosts`:
```
127.0.0.1 watupa.local
```
Access at: http://watupa.local

### 6. Verify Deployment
```bash
kubectl get pods -n watupa-app
kubectl get pods -n watupa-data
kubectl get services -n watupa-app
kubectl logs -n watupa-app deployment/frontend
```

## Troubleshooting

### Common Issues

1. **Pods not starting**
   - Check image builds: `docker images | grep watupa`
   - Check pod status: `kubectl describe pod <pod-name> -n watupa-app`

2. **Database connection errors**
   - Ensure Postgres pod is running: `kubectl logs -n watupa-data deployment/postgres`
   - Check DB credentials in secret

3. **Service communication issues**
   - Verify services: `kubectl get svc -n watupa-app`
   - Check env vars in pods: `kubectl exec -n watupa-app <pod> -- env`

4. **Frontend not loading**
   - Check BFF service: `kubectl logs -n watupa-app deployment/bff-service`
   - Verify API_URL in frontend pod

5. **Port conflicts**
   - Ensure no local services on ports 3000, 8000-8005, 5432

### Logs and Debugging
```bash
# All pods
kubectl logs -n watupa-app --all-containers
kubectl logs -n watupa-data --all-containers

# Specific service
kubectl logs -n watupa-app deployment/<service-name>

# Describe pod
kubectl describe pod <pod-name> -n watupa-app
```

### Cleanup
```bash
# Delete namespaces
kubectl delete namespace watupa-app
kubectl delete namespace watupa-data

# Delete clusters (if using k3d)
k3d cluster delete watupa-cluster
```

## Development Notes

- All original ports and API endpoints are preserved
- Services use Kubernetes DNS for internal communication
- Database schema is initialized via ConfigMap
- Frontend proxies API calls to BFF service
- CORS is configured for local development

For production deployment, update Ingress host and add TLS certificates.
