# watupa.lk — Local Minikube Deployment Guide (Windows)

**Run Minikube locally on Windows.**

Your Windows machine needs:
- 8 GB RAM minimum (16 GB recommended)
- 4 CPU cores
- 20 GB free disk space
- Windows 10/11 64-bit

---

## Prerequisites — Install on Windows

### 1. Install Docker Desktop
Download from: https://www.docker.com/products/docker-desktop/

During install:
- ✅ Enable WSL 2 backend (recommended)
- ✅ Add Docker to PATH

After install, open Docker Desktop and wait for it to start (green icon).

### 2. Install kubectl
Open **PowerShell as Administrator** and run:
```powershell
# Using winget (Windows Package Manager)
winget install -e --id Kubernetes.kubectl

# Verify
kubectl version --client
```

Or download manually from:
https://dl.k8s.io/release/v1.30.0/bin/windows/amd64/kubectl.exe
→ place in `C:\Windows\System32\`

### 3. Install Minikube
```powershell
# Using winget
winget install -e --id Kubernetes.minikube

# Verify
minikube version
```

Or download from: https://minikube.sigs.k8s.io/docs/start/
→ Run the `.exe` installer

### 4. Install Java 21 (for building backend)
Download from: https://adoptium.net/temurin/releases/?version=21
→ Install the `.msi` package, tick "Set JAVA_HOME"

```powershell
java -version   # should show openjdk 21
mvn -version    # should show Maven 3.x
```

### 5. Install Node.js 20 (for building frontend)
Download from: https://nodejs.org/en/download
→ Install the LTS Windows Installer (.msi)

```powershell
node -v    # v20.x.x
npm -v     # 10.x.x
```

### 6. Install Git (if not already installed)
```powershell
winget install -e --id Git.Git
```

---

## Step 1 — Clone the Repository

Open **PowerShell** (normal user, not admin) and run:

```powershell
git clone <YOUR_REPO_URL> watupa-lk
cd watupa-lk
```

---

## Step 2 — Start Minikube

```powershell
# Start Minikube with enough resources
# Adjust --memory based on your machine:
#   8GB RAM machine  → use 5120 (5GB)
#   16GB RAM machine → use 8192 (8GB)


minikube start `
  --driver=docker `
  --cpus=4 `
  --memory=6144 `
  --disk-size=20g `
  --addons=ingress

# Verify it's running
minikube status
kubectl get nodes
```

Expected output:
```
NAME       STATUS   ROLES           AGE   VERSION
minikube   Ready    control-plane   1m    v1.30.x
```

**Enable the ingress addon:**
```powershell
minikube addons enable ingress

# Wait for ingress controller (~180 seconds)
kubectl wait --namespace ingress-nginx `
  --for=condition=ready pod `
  --selector=app.kubernetes.io/component=controller `
  --timeout=180s
```

---

## Step 3 — Point PowerShell to Minikube's Docker

This lets Docker Desktop build images directly into Minikube's registry,
so you don't need to push to Docker Hub.

```powershell
# Run this — it sets 3 environment variables in your current PowerShell session
# Point your shell's docker commands at minikube's daemon
minikube -p minikube docker-env --shell powershell | Invoke-Expression

# Verify (you should see minikube internal containers)
docker ps
```

> ⚠️ You must run this in **every new PowerShell window** you open.
> To make it permanent, add it to your PowerShell profile.

---

## Step 4 — Build All Docker Images

Run all commands from the **repository root** (`watupa-lk\`).

### Backend services (6 services)
# Now build — the image lands inside minikube directly

```powershell
# Identity Service
docker build `
  --platform linux/amd64 `
  -f k8s\dockerfiles\Dockerfile.identity-service `
  -t watupa/identity-service:latest `
  backend\

# Salary Submission Service
docker build `
  --platform linux/amd64 `
  -f k8s\dockerfiles\Dockerfile.salary-submission-service `
  -t watupa/salary-submission-service:latest `
  backend\

# Search Service
docker build `
  --platform linux/amd64 `
  -f k8s\dockerfiles\Dockerfile.search-service `
  -t watupa/search-service:latest `
  backend\

# Stats Service
docker build `
  --platform linux/amd64 `
  -f k8s\dockerfiles\Dockerfile.stats-service `
  -t watupa/stats-service:latest `
  backend\

# Vote Service
docker build `
  --platform linux/amd64 `
  -f k8s\dockerfiles\Dockerfile.vote-service `
  -t watupa/vote-service:latest `
  backend\

# BFF Service
docker build `
  --platform linux/amd64 `
  -f k8s\dockerfiles\Dockerfile.bff-service `
  -t watupa/bff-service:latest `
  backend\
```

### Frontend

```powershell
docker build `
  -f k8s\dockerfiles\Dockerfile.frontend `
  -t watupa/frontend:latest `
  frontend\
```

### Verify all 7 images are built
```powershell
# Verify minikube can see it
minikube image ls | Select-String "watupa"
```

Expected:
```
watupa/frontend                  latest   ...
watupa/bff-service               latest   ...
watupa/vote-service              latest   ...
watupa/stats-service             latest   ...
watupa/search-service            latest   ...
watupa/salary-submission-service latest   ...
watupa/identity-service          latest   ...
```

---

## Step 5 — Apply Kubernetes Manifests

```powershell
# 1. Namespaces first
kubectl apply -f k8s\namespaces\namespaces.yaml

# 2. Secrets (DB credentials, JWT)
kubectl apply -f k8s\secrets\postgres-secret.yaml
kubectl apply -f k8s\secrets\app-secrets.yaml

# 3. ConfigMaps (service URLs, CORS)
kubectl apply -f k8s\configmaps\app-config.yaml

# 4. PostgreSQL in data namespace
kubectl apply -f k8s\postgres\postgres.yaml

# 5. Wait for PostgreSQL to be ready
Write-Host "Waiting for PostgreSQL to be ready..."
kubectl wait --namespace data `
  --for=condition=ready pod `
  --selector=app=postgres `
  --timeout=120s

# 6. Verify DB schemas were initialised
kubectl exec -n data deploy/postgres -- `
  psql -U watupa -d techsalary -c "\dn"

# 7. Deploy all microservices + frontend
kubectl apply -f k8s\deployments\app-deployments.yaml

# 8. Apply ingress rules
kubectl apply -f k8s\ingress\ingress.yaml
```

---

## Step 6 — Verify Everything is Running

```powershell
# Check all pods
kubectl get pods -n data
kubectl get pods -n app

# Check services
kubectl get services -n app
kubectl get services -n data

# Check ingress
kubectl get ingress -n app
```

Wait until all pods show `1/1 Running`. This takes 2–4 minutes for the
Spring Boot services to fully start.

Watch live:
```powershell
kubectl get pods -n app --watch
# Press Ctrl+C when all are Running
```

---

## Step 7 — Access the Application

### Option A — Minikube Tunnel (Recommended for Windows)

Open a **new PowerShell window** (keep it open) and run:
```powershell
minikube tunnel
# Enter admin password if prompted
# Keep this window open — it must stay running
```

Then open your browser:
```
http://localhost/
```

### Option B — Minikube Service URL
```powershell
minikube service frontend-service -n app --url
# Opens the frontend in your browser automatically
```

### Option C — Port forward manually
```powershell
# Forward frontend to localhost:3000
kubectl port-forward -n app service/frontend-service 3000:3000

# Open: http://localhost:3000
```

For the BFF in a separate terminal:
```powershell
kubectl port-forward -n app service/bff-service 8000:8000
```

---

## Step 8 — Test the Full Workflow (End-to-End)

Open PowerShell and run these commands to prove the full workflow.

### 1. Submit a salary (anonymous, no login needed)
```powershell
$MINIKUBE_IP = minikube ip

Invoke-RestMethod -Uri "http://$MINIKUBE_IP/bff/api/submissions" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{
    "companyName": "WSO2",
    "jobTitle": "Software Engineer",
    "experienceLevel": "MID",
    "employmentType": "FULL_TIME",
    "country": "Sri Lanka",
    "currency": "LKR",
    "baseSalary": 150000,
    "totalCompensation": 180000,
    "anonymize": false
  }' | ConvertTo-Json
```

Expected: `status: "PENDING"`

### 2. Verify it's PENDING in the database
```powershell
kubectl exec -n data deploy/postgres -- `
  psql -U watupa -d techsalary `
  -c "SELECT id, job_title, status FROM salary.submission;"
```

### 3. Sign up a user
```powershell
Invoke-RestMethod -Uri "http://$MINIKUBE_IP/bff/api/auth/signup" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"testuser","email":"test@example.com","password":"pass123"}'
```

### 4. Login and capture the JWT token
```powershell
$loginResponse = Invoke-RestMethod `
  -Uri "http://$MINIKUBE_IP/bff/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"testuser","password":"pass123"}'

$TOKEN = $loginResponse.data.token
Write-Host "Token: $TOKEN"
```

### 5. Vote (need 2 votes to reach APPROVED threshold)
```powershell
# Vote from user 1
Invoke-RestMethod -Uri "http://$MINIKUBE_IP/bff/api/votes" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $TOKEN" } `
  -Body '{"submissionId": 1, "voteType": "UPVOTE"}'

# Create a second user
Invoke-RestMethod -Uri "http://$MINIKUBE_IP/bff/api/auth/signup" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"user2","email":"user2@example.com","password":"pass456"}'

$login2 = Invoke-RestMethod `
  -Uri "http://$MINIKUBE_IP/bff/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"user2","password":"pass456"}'

$TOKEN2 = $login2.data.token

# Vote from user 2 — this reaches the threshold (2 votes)
Invoke-RestMethod -Uri "http://$MINIKUBE_IP/bff/api/votes" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $TOKEN2" } `
  -Body '{"submissionId": 1, "voteType": "UPVOTE"}'
```

### 6. Verify submission is now APPROVED
```powershell
kubectl exec -n data deploy/postgres -- `
  psql -U watupa -d techsalary `
  -c "SELECT id, job_title, status FROM salary.submission WHERE id = 1;"
```
Expected: `APPROVED`

### 7. Search for approved salaries
```powershell
Invoke-RestMethod -Uri "http://$MINIKUBE_IP/bff/api/search?country=Sri+Lanka" |
  ConvertTo-Json -Depth 5
```

### 8. Get statistics
```powershell
Invoke-RestMethod -Uri "http://$MINIKUBE_IP/bff/api/stats" |
  ConvertTo-Json -Depth 5
```

---

## Useful Commands (Windows PowerShell)

### Pod status
```powershell
kubectl get pods -n app
kubectl get pods -n data
kubectl get pods --all-namespaces
```

### View logs
```powershell
kubectl logs -n app deploy/identity-service -f
kubectl logs -n app deploy/bff-service -f
kubectl logs -n data deploy/postgres -f
```

### Describe a pod (for debugging)
```powershell
kubectl describe pod -n app -l app=identity-service
```

### Restart a deployment
```powershell
kubectl rollout restart deployment/identity-service -n app
```

### Check DB content
```powershell
# List all tables
kubectl exec -n data deploy/postgres -- `
  psql -U watupa -d techsalary -c "\dt identity.*"

kubectl exec -n data deploy/postgres -- `
  psql -U watupa -d techsalary -c "\dt salary.*"

kubectl exec -n data deploy/postgres -- `
  psql -U watupa -d techsalary -c "\dt community.*"

# Count records
kubectl exec -n data deploy/postgres -- `
  psql -U watupa -d techsalary `
  -c "SELECT COUNT(*) FROM salary.submission WHERE status='APPROVED';"
```

### Stop Minikube (keeps all data)
```powershell
minikube stop
```

### Delete everything and start fresh
```powershell
kubectl delete -f k8s\deployments\app-deployments.yaml
kubectl delete -f k8s\ingress\ingress.yaml
kubectl delete -f k8s\postgres\postgres.yaml
kubectl delete -f k8s\configmaps\
kubectl delete -f k8s\secrets\
kubectl delete namespace app
kubectl delete namespace data
```

---

## Troubleshooting

### "minikube start" fails — not enough memory
```
Error: ... insufficient memory
```
Lower the memory: `minikube start --memory=4096 --cpus=2`

### Pod stuck in `Pending`
```powershell
kubectl describe pod <pod-name> -n app
# Check "Events:" — usually: "Insufficient memory"
```
Solution: `minikube stop` then `minikube start --memory=8192`

### `CrashLoopBackOff` on a service
```powershell
kubectl logs <pod-name> -n app --previous
```
Usually a DB connection issue. Check that `app-secrets` has correct values
and that PostgreSQL is running: `kubectl get pods -n data`

### `ErrImageNeverPull` — image not found
```powershell
# You must point Docker to minikube BEFORE building
& minikube -p minikube docker-env --shell powershell | Invoke-Expression
# Then rebuild the image
docker build -f k8s\dockerfiles\Dockerfile.identity-service `
  -t watupa/identity-service:latest backend\identity-service\
```

### Ingress not routing
```powershell
kubectl get pods -n ingress-nginx
# If not running: minikube addons enable ingress
```

### Port already in use (tunnel error)
```powershell
# Kill whatever is on port 80
netstat -ano | findstr :80
taskkill /PID <PID> /F
```

---

## System Requirements Summary

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| RAM | 8 GB | 16 GB |
| CPU | 4 cores | 6+ cores |
| Disk free | 15 GB | 30 GB |
| OS | Windows 10 64-bit | Windows 11 |
| Docker Desktop | 4.x+ | Latest |
| Minikube RAM allocation | 5 GB | 6–8 GB |
| Minikube CPU allocation | 2 | 4 |

---

## File Structure

```
watupa-lk\
├── backend\
│   ├── identity-service\
│   ├── salary-submission-service\
│   ├── search-service\
│   ├── stats-service\
│   ├── vote-service\
│   └── bff-service\
├── frontend\
├── database\
│   └── postgres\init\001_init.sql
└── k8s\
    ├── namespaces\namespaces.yaml
    ├── secrets\
    │   ├── postgres-secret.yaml
    │   └── app-secrets.yaml
    ├── configmaps\app-config.yaml
    ├── postgres\postgres.yaml
    ├── deployments\app-deployments.yaml
    ├── ingress\ingress.yaml
    └── dockerfiles\
        ├── Dockerfile.identity-service
        ├── Dockerfile.salary-submission-service
        ├── Dockerfile.search-service
        ├── Dockerfile.stats-service
        ├── Dockerfile.vote-service
        ├── Dockerfile.bff-service
        └── Dockerfile.frontend
```
