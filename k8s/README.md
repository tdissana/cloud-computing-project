# watupa.lk — Minikube Deployment Guide (Azure VM)

Complete step-by-step guide to deploy the watupa.lk microservices platform
on a single-node Kubernetes cluster using Minikube on an Azure VM.

---

## Architecture Overview

```
Internet
    │
    ▼
Azure VM (Public IP)
    │  port 80 (nginx ingress via NodePort)
    ▼
Minikube (single-node k8s)
    │
    ├── ingress-nginx namespace
    │     └── Ingress controller
    │           ├── /bff/*  → bff-service:8000
    │           └── /*      → frontend-service:3000
    │
    ├── app namespace
    │     ├── frontend          :3000
    │     ├── bff-service       :8000
    │     ├── identity-service  :8001
    │     ├── salary-submission :8002
    │     ├── search-service    :8003
    │     ├── stats-service     :8004
    │     └── vote-service      :8005
    │
    └── data namespace
          └── postgres          :5432  (PVC: 5Gi)
```

---

## Prerequisites on Azure VM

### 1. Create Azure VM
- **OS**: Ubuntu 22.04 LTS
- **Size**: Standard_D4s_v3 (4 vCPUs, 16GB RAM) or larger
- **Disk**: 64GB minimum
- **Networking**: Open inbound ports: 22 (SSH), 80 (HTTP), 443 (HTTPS)

### 2. SSH into your VM
```bash
ssh azureuser@<YOUR_VM_PUBLIC_IP>
```

### 3. Install Docker
```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg

sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

echo "deb [arch=$(dpkg --print-architecture) \
  signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Add your user to docker group (re-login required after this)
sudo usermod -aG docker $USER
newgrp docker

# Verify
docker --version
```

### 4. Install kubectl
```bash
curl -LO "https://dl.k8s.io/release/$(curl -sL \
  https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
kubectl version --client
```

### 5. Install Minikube
```bash
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube
minikube version
```

### 6. Install Java 21 & Maven (for building backend)
```bash
sudo apt-get install -y openjdk-21-jdk maven
java -version
mvn -version
```

### 7. Install Node.js 20 (for building frontend)
```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs
node -v
npm -v
```

---

## Step 1 — Clone the Repository

```bash
git clone <YOUR_REPO_URL> watupa-lk
cd watupa-lk
```

---

## Step 2 — Start Minikube

```bash
# Start with enough resources for 8 services
minikube start \
  --driver=docker \
  --cpus=4 \
  --memory=8192 \
  --disk-size=30g

# Verify it's running
minikube status
kubectl get nodes
```

**Enable the ingress addon:**
```bash
minikube addons enable ingress

# Wait for ingress controller to be ready (~60 seconds)
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s
```

---

## Step 3 — Point Docker to Minikube's Registry

This makes images built with Docker available inside Minikube without pushing
to an external registry.

```bash
# Configure your shell to use minikube's Docker daemon
eval $(minikube docker-env)

# Verify (should show minikube containers)
docker ps
```

> ⚠️ Run this in **every new terminal** you open, or add it to `~/.bashrc`.

---

## Step 4 — Build All Docker Images

Run all commands from the **repository root**.

### Backend services

```bash
# Identity Service
docker build \
  -f k8s/dockerfiles/Dockerfile.identity-service \
  -t watupa/identity-service:latest \
  backend/identity-service/

# Salary Submission Service
docker build \
  -f k8s/dockerfiles/Dockerfile.salary-submission-service \
  -t watupa/salary-submission-service:latest \
  backend/salary-submission-service/

# Search Service
docker build \
  -f k8s/dockerfiles/Dockerfile.search-service \
  -t watupa/search-service:latest \
  backend/search-service/

# Stats Service
docker build \
  -f k8s/dockerfiles/Dockerfile.stats-service \
  -t watupa/stats-service:latest \
  backend/stats-service/

# Vote Service
docker build \
  -f k8s/dockerfiles/Dockerfile.vote-service \
  -t watupa/vote-service:latest \
  backend/vote-service/

# BFF Service
docker build \
  -f k8s/dockerfiles/Dockerfile.bff-service \
  -t watupa/bff-service:latest \
  backend/bff-service/
```

### Frontend

```bash
docker build \
  -f k8s/dockerfiles/Dockerfile.frontend \
  -t watupa/frontend:latest \
  frontend/
```

### Verify all images are built
```bash
docker images | grep watupa
```

Expected output:
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

Apply in this exact order (dependencies first):

```bash
# 1. Namespaces
kubectl apply -f k8s/namespaces/namespaces.yaml

# 2. Secrets
kubectl apply -f k8s/secrets/postgres-secret.yaml
kubectl apply -f k8s/secrets/app-secrets.yaml

# 3. ConfigMaps
kubectl apply -f k8s/configmaps/app-config.yaml

# 4. PostgreSQL (data namespace)
kubectl apply -f k8s/postgres/postgres.yaml

# 5. Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL..."
kubectl wait --namespace data \
  --for=condition=ready pod \
  --selector=app=postgres \
  --timeout=120s

# 6. Verify DB schema was initialised
kubectl exec -n data deploy/postgres -- \
  psql -U watupa -d techsalary -c "\dn"

# 7. All microservices + frontend
kubectl apply -f k8s/deployments/app-deployments.yaml

# 8. Ingress rules
kubectl apply -f k8s/ingress/ingress.yaml
```

---

## Step 6 — Verify All Pods Are Running

```bash
# Check all namespaces
kubectl get pods -n data
kubectl get pods -n app
kubectl get ingress -n app

# Watch pods starting up (Ctrl+C to stop)
kubectl get pods -n app --watch
```

All pods should show `Running` and `1/1` READY. This may take 2–3 minutes.

If a pod is not starting:
```bash
kubectl describe pod <pod-name> -n app
kubectl logs <pod-name> -n app
```

---

## Step 7 — Access the Application

### Get the Minikube IP
```bash
minikube ip
# Example output: 192.168.49.2
```

### Option A — Access via Minikube IP (within the VM)
```bash
MINIKUBE_IP=$(minikube ip)
curl http://$MINIKUBE_IP/
```

### Option B — Port-forward for browser access from your laptop

On the **Azure VM**, run:
```bash
# Forward minikube's ingress NodePort to VM port 80
sudo kubectl port-forward \
  --namespace ingress-nginx \
  service/ingress-nginx-controller 80:80 \
  --address 0.0.0.0 &
```

Then open in your browser:
```
http://<AZURE_VM_PUBLIC_IP>/
```

### Option C — Minikube tunnel (alternative)
```bash
# Run in a separate terminal — keeps running
minikube tunnel
# App available at: http://127.0.0.1/
```

---

## Step 8 — Test the Full Workflow

### 1. Submit a salary (no login required)
```bash
MINIKUBE_IP=$(minikube ip)

curl -X POST http://$MINIKUBE_IP/bff/api/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "WSO2",
    "jobTitle": "Software Engineer",
    "experienceLevel": "MID",
    "employmentType": "FULL_TIME",
    "country": "Sri Lanka",
    "currency": "LKR",
    "baseSalary": 150000,
    "totalCompensation": 180000,
    "anonymize": false
  }'
```
Expected: `{"status":"PENDING", "id": 1, ...}`

### 2. Verify it's stored as PENDING
```bash
kubectl exec -n data deploy/postgres -- \
  psql -U watupa -d techsalary \
  -c "SELECT id, job_title, status FROM salary.submission;"
```

### 3. Sign up a user
```bash
curl -X POST http://$MINIKUBE_IP/bff/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "pass123"
  }'
```

### 4. Login and get JWT token
```bash
TOKEN=$(curl -s -X POST http://$MINIKUBE_IP/bff/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"pass123"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")

echo "Token: $TOKEN"
```

### 5. Vote on the submission (threshold = 2 votes)
```bash
# Vote 1
curl -X POST http://$MINIKUBE_IP/bff/api/votes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"submissionId": 1, "voteType": "UPVOTE"}'

# Sign up a second user and vote again to reach threshold
curl -X POST http://$MINIKUBE_IP/bff/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"user2","email":"user2@example.com","password":"pass456"}'

TOKEN2=$(curl -s -X POST http://$MINIKUBE_IP/bff/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user2","password":"pass456"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")

curl -X POST http://$MINIKUBE_IP/bff/api/votes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN2" \
  -d '{"submissionId": 1, "voteType": "UPVOTE"}'
```

### 6. Verify submission is now APPROVED
```bash
kubectl exec -n data deploy/postgres -- \
  psql -U watupa -d techsalary \
  -c "SELECT id, job_title, status FROM salary.submission WHERE id = 1;"
```
Expected: `status = APPROVED`

### 7. Search for approved salaries
```bash
curl "http://$MINIKUBE_IP/bff/api/search?country=Sri+Lanka"
```

### 8. Check stats
```bash
curl "http://$MINIKUBE_IP/bff/api/stats"
```

---

## Useful Commands

### Check pod status
```bash
kubectl get pods -n app -o wide
kubectl get pods -n data -o wide
```

### View logs
```bash
kubectl logs -n app deploy/identity-service --follow
kubectl logs -n app deploy/bff-service --follow
kubectl logs -n data deploy/postgres --follow
```

### Describe a pod (for debugging)
```bash
kubectl describe pod -n app -l app=identity-service
```

### Check services and ingress
```bash
kubectl get services -n app
kubectl get services -n data
kubectl get ingress -n app
```

### Restart a deployment
```bash
kubectl rollout restart deployment/identity-service -n app
```

### Check DB schemas
```bash
kubectl exec -n data deploy/postgres -- \
  psql -U watupa -d techsalary -c "\dn"

kubectl exec -n data deploy/postgres -- \
  psql -U watupa -d techsalary -c "\dt identity.*"

kubectl exec -n data deploy/postgres -- \
  psql -U watupa -d techsalary -c "\dt salary.*"

kubectl exec -n data deploy/postgres -- \
  psql -U watupa -d techsalary -c "\dt community.*"
```

### Delete everything and start over
```bash
kubectl delete -f k8s/deployments/app-deployments.yaml
kubectl delete -f k8s/ingress/ingress.yaml
kubectl delete -f k8s/postgres/postgres.yaml
kubectl delete -f k8s/configmaps/
kubectl delete -f k8s/secrets/
kubectl delete -f k8s/namespaces/namespaces.yaml
```

### Stop/Delete minikube
```bash
minikube stop       # pause, keeps data
minikube delete     # full wipe
```

---

## Troubleshooting

### Pod stuck in `Pending`
```bash
kubectl describe pod <pod-name> -n app
# Look for "Events:" section — usually insufficient CPU/memory
# Fix: minikube start --cpus=4 --memory=8192
```

### Pod in `CrashLoopBackOff`
```bash
kubectl logs <pod-name> -n app --previous
# Usually a DB connection issue — check app-secrets
```

### Cannot connect to PostgreSQL
```bash
# Test from inside the cluster
kubectl run pg-test --image=postgres:16-alpine -n app --rm -it --restart=Never -- \
  psql -h postgres-service.data.svc.cluster.local -U watupa -d techsalary -c "\dn"
```

### Ingress not working
```bash
kubectl get pods -n ingress-nginx
# Make sure ingress addon is enabled: minikube addons enable ingress
```

### Image not found (`ErrImagePull`)
```bash
# Make sure you ran: eval $(minikube docker-env)
# Then rebuild: docker build -t watupa/<service>:latest ...
# Check: docker images | grep watupa
```

---

## Repository Structure
```
watupa-lk/
├── backend/
│   ├── identity-service/
│   ├── salary-submission-service/
│   ├── search-service/
│   ├── stats-service/
│   ├── vote-service/
│   └── bff-service/
├── frontend/
├── database/
│   └── postgres/
│       └── init/001_init.sql
└── k8s/
    ├── namespaces/namespaces.yaml
    ├── secrets/
    │   ├── postgres-secret.yaml
    │   └── app-secrets.yaml
    ├── configmaps/app-config.yaml
    ├── postgres/postgres.yaml          ← PVC + Deployment + Service
    ├── deployments/app-deployments.yaml ← all 7 services
    ├── ingress/ingress.yaml
    └── dockerfiles/
        ├── Dockerfile.identity-service
        ├── Dockerfile.salary-submission-service
        ├── Dockerfile.search-service
        ├── Dockerfile.stats-service
        ├── Dockerfile.vote-service
        ├── Dockerfile.bff-service
        └── Dockerfile.frontend
```
