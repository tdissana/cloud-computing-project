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
kubectl get pods -n watupa
kubectl get services -n watupa
kubectl logs -n watupa deployment/frontend
```

## Troubleshooting

### Common Issues

1. **Pods not starting**
   - Check image builds: `docker images | grep watupa`
   - Check pod status: `kubectl describe pod <pod-name> -n watupa`

2. **Database connection errors**
   - Ensure Postgres pod is running: `kubectl logs -n watupa deployment/postgres`
   - Check DB credentials in secret

3. **Service communication issues**
   - Verify services: `kubectl get svc -n watupa`
   - Check env vars in pods: `kubectl exec -n watupa <pod> -- env`

4. **Frontend not loading**
   - Check BFF service: `kubectl logs -n watupa deployment/bff-service`
   - Verify API_URL in frontend pod

5. **Port conflicts**
   - Ensure no local services on ports 3000, 8000-8005, 5432

### Logs and Debugging
```bash
# All pods
kubectl logs -n watupa --all-containers

# Specific service
kubectl logs -n watupa deployment/<service-name>

# Describe pod
kubectl describe pod <pod-name> -n watupa
```

### Cleanup
```bash
kubectl delete namespace watupa
k3d cluster delete watupa-cluster  # if using k3d
```

## Development Notes

- All original ports and API endpoints are preserved
- Services use Kubernetes DNS for internal communication
- Database schema is initialized via ConfigMap
- Frontend proxies API calls to BFF service
- CORS is configured for local development

For production deployment, update Ingress host and add TLS certificates.
