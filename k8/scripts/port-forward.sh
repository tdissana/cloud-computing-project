#!/bin/bash
# Port forward frontend to localhost:3000
kubectl port-forward -n watupa-app svc/frontend 3000:3000 &
# Port forward bff to localhost:8000 if needed
kubectl port-forward -n watupa-app svc/bff-service 8000:8000 &
echo "Port forwarding started. Access frontend at http://localhost:3000"
