#!/bin/bash
set -e

# Apply manifests
kubectl apply -f k8/manifests/namespace.yaml
kubectl apply -f k8/manifests/configmap.yaml
kubectl apply -f k8/manifests/secret.yaml
kubectl apply -f k8/manifests/postgres.yaml
kubectl wait --for=condition=ready pod -l app=postgres -n watupa-data --timeout=120s
kubectl apply -f k8/manifests/bff-service.yaml
kubectl apply -f k8/manifests/identity-service.yaml
kubectl apply -f k8/manifests/salary-submission-service.yaml
kubectl apply -f k8/manifests/search-service.yaml
kubectl apply -f k8/manifests/stats-service.yaml
kubectl apply -f k8/manifests/vote-service.yaml
kubectl apply -f k8/manifests/frontend.yaml

echo "Deployment complete. Waiting for pods to be ready..."
kubectl wait --for=condition=ready pod --all -n watupa-app --timeout=60s
kubectl wait --for=condition=ready pod --all -n watupa-data --timeout=60s
echo "All pods are ready!"
