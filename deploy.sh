#!/usr/bin/env bash
# =======================================================================
# deploy.sh — Apply all Kubernetes manifests in correct order
# Run from the watupa-lk project root AFTER build-images.sh
# =======================================================================
set -euo pipefail

K8S="$(cd "$(dirname "$0")" && pwd)/k8s"
KUBECTL="sudo kubectl"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Watupa-LK — Kubernetes deploy"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# ── Step 1: Namespaces ────────────────────────────────────────────────
echo "▶ [1/6] Creating namespaces..."
$KUBECTL apply -f "$K8S/namespaces/namespaces.yaml"

# ── Step 2: Secrets ───────────────────────────────────────────────────
echo "▶ [2/6] Applying secrets..."
$KUBECTL apply -f "$K8S/secrets/postgres-secret.yaml"
$KUBECTL apply -f "$K8S/secrets/app-secrets.yaml"

# ── Step 3: ConfigMaps ────────────────────────────────────────────────
echo "▶ [3/6] Applying configmaps..."
$KUBECTL apply -f "$K8S/configmaps/app-config.yaml"

# ── Step 4: PostgreSQL (data namespace) ───────────────────────────────
echo "▶ [4/6] Deploying PostgreSQL..."
$KUBECTL apply -f "$K8S/postgres/postgres.yaml"

echo "   Waiting for PostgreSQL to be ready..."
$KUBECTL rollout status deployment/postgres -n data --timeout=120s

# ── Step 5: Application services (app namespace) ──────────────────────
echo "▶ [5/6] Deploying application services..."
$KUBECTL apply -f "$K8S/deployments/app-deployments.yaml"

echo "   Waiting for all deployments to roll out..."
for svc in identity salary-submission vote search stats bff frontend; do
  echo "   → $svc"
  $KUBECTL rollout status deployment/$svc -n app --timeout=180s
done

# ── Step 6: Ingress ───────────────────────────────────────────────────
echo "▶ [6/6] Applying ingress rules..."
$KUBECTL apply -f "$K8S/ingress/ingress.yaml"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Deployment complete!"
echo ""
echo "  Pods in app namespace:"
$KUBECTL get pods -n app
echo ""
echo "  Pods in data namespace:"
$KUBECTL get pods -n data
echo ""
echo "  Ingress:"
$KUBECTL get ingress -n app
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
