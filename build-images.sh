#!/usr/bin/env bash
# =======================================================================
# build-images.sh — Build all Watupa-LK Docker images on the Azure VM
# Run this from the root of the watupa-lk project directory
# =======================================================================
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
DOCKERFILE_DIR="$PROJECT_ROOT/k8s/dockerfiles"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Watupa-LK — Docker image builder"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# ── 1. Identity Service ───────────────────────────────────────────────
echo ""
echo "▶ Building identity-service..."
docker build \
  -f "$DOCKERFILE_DIR/Dockerfile.identity-service" \
  -t watupa/identity-service:latest \
  "$PROJECT_ROOT/backend"

# ── 2. Salary Submission Service ──────────────────────────────────────
echo ""
echo "▶ Building salary-submission-service..."
docker build \
  -f "$DOCKERFILE_DIR/Dockerfile.salary-submission-service" \
  -t watupa/salary-submission-service:latest \
  "$PROJECT_ROOT/backend"

# ── 3. Vote Service ───────────────────────────────────────────────────
echo ""
echo "▶ Building vote-service..."
docker build \
  -f "$DOCKERFILE_DIR/Dockerfile.vote-service" \
  -t watupa/vote-service:latest \
  "$PROJECT_ROOT/backend"

# ── 4. Search Service ─────────────────────────────────────────────────
echo ""
echo "▶ Building search-service..."
docker build \
  -f "$DOCKERFILE_DIR/Dockerfile.search-service" \
  -t watupa/search-service:latest \
  "$PROJECT_ROOT/backend"

# ── 5. Stats Service ──────────────────────────────────────────────────
echo ""
echo "▶ Building stats-service..."
docker build \
  -f "$DOCKERFILE_DIR/Dockerfile.stats-service" \
  -t watupa/stats-service:latest \
  "$PROJECT_ROOT/backend"

# ── 6. BFF Service ────────────────────────────────────────────────────
echo ""
echo "▶ Building bff-service..."
docker build \
  -f "$DOCKERFILE_DIR/Dockerfile.bff-service" \
  -t watupa/bff-service:latest \
  "$PROJECT_ROOT/backend"

# ── 7. Frontend ───────────────────────────────────────────────────────
echo ""
echo "▶ Building frontend..."
docker build \
  -f "$DOCKERFILE_DIR/Dockerfile.frontend" \
  -t watupa/frontend:latest \
  "$PROJECT_ROOT/frontend"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  All images built. Importing into k3s containerd..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# k3s uses containerd, NOT the Docker daemon.
# Images must be explicitly imported with ctr.
IMAGES=(
  "watupa/identity-service:latest"
  "watupa/salary-submission-service:latest"
  "watupa/vote-service:latest"
  "watupa/search-service:latest"
  "watupa/stats-service:latest"
  "watupa/bff-service:latest"
  "watupa/frontend:latest"
)

for img in "${IMAGES[@]}"; do
  echo "  → Importing $img into k3s containerd..."
  docker save "$img" | sudo k3s ctr images import -
done

echo ""
echo "✅ All images imported into k3s containerd."
echo "   Verify with: sudo k3s ctr images list | grep watupa"
