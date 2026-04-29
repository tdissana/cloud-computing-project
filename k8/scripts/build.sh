#!/bin/bash
set -e

# Build backend services
echo "Building backend services..."
docker build -f k8/dockerfiles/bff-service-Dockerfile -t watupa/bff-service:latest .
docker build -f k8/dockerfiles/identity-service-Dockerfile -t watupa/identity-service:latest .
docker build -f k8/dockerfiles/salary-submission-service-Dockerfile -t watupa/salary-submission-service:latest .
docker build -f k8/dockerfiles/search-service-Dockerfile -t watupa/search-service:latest .
docker build -f k8/dockerfiles/stats-service-Dockerfile -t watupa/stats-service:latest .
docker build -f k8/dockerfiles/vote-service-Dockerfile -t watupa/vote-service:latest .

# Build frontend
echo "Building frontend..."
docker build -f k8/dockerfiles/frontend-Dockerfile -t watupa/frontend:latest .

# Load images into k3d cluster
echo "Loading images into k3d cluster..."
k3d image import watupa/bff-service:latest -c watupa-cluster
k3d image import watupa/identity-service:latest -c watupa-cluster
k3d image import watupa/salary-submission-service:latest -c watupa-cluster
k3d image import watupa/search-service:latest -c watupa-cluster
k3d image import watupa/stats-service:latest -c watupa-cluster
k3d image import watupa/vote-service:latest -c watupa-cluster
k3d image import watupa/frontend:latest -c watupa-cluster

echo "Build complete. All images loaded into k3d cluster."

