#!/bin/bash
set -e

# Build backend services
docker build -f k8/dockerfiles/bff-service-Dockerfile -t watupa/bff-service:latest .
docker build -f k8/dockerfiles/identity-service-Dockerfile -t watupa/identity-service:latest .
docker build -f k8/dockerfiles/salary-submission-service-Dockerfile -t watupa/salary-submission-service:latest .
docker build -f k8/dockerfiles/search-service-Dockerfile -t watupa/search-service:latest .
docker build -f k8/dockerfiles/stats-service-Dockerfile -t watupa/stats-service:latest .
docker build -f k8/dockerfiles/vote-service-Dockerfile -t watupa/vote-service:latest .

# Build frontend
docker build -f k8/dockerfiles/frontend-Dockerfile -t watupa/frontend:latest .
