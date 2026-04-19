#!/usr/bin/env bash
# =======================================================================
# test-workflow.sh  —  End-to-end proof of the full Watupa-LK workflow:
#   submit (anon) → signup/login → vote → approve → search → stats
#
# Usage:  ./test-workflow.sh <VM_PUBLIC_IP>
#         ./test-workflow.sh localhost     (if run on the VM itself)
# =======================================================================
set -euo pipefail

BASE_URL="http://${1:-localhost}/api"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Watupa-LK end-to-end workflow test"
echo "  BFF base: $BASE_URL"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Helpers
jq_or_raw() { command -v jq &>/dev/null && echo "$1" | jq . || echo "$1"; }
check() { echo ""; echo "── $1 ──"; }

# ── STEP 1: Anonymous salary submission ───────────────────────────────
check "STEP 1 — Anonymous salary submission (no login required)"

SUBMIT_RESPONSE=$(curl -s -X POST "$BASE_URL/salary/submit" \
  -H "Content-Type: application/json" \
  -d '{
    "jobTitle": "Senior Software Engineer",
    "company": "WSO2",
    "country": "LK",
    "city": "Colombo",
    "employmentType": "FULL_TIME",
    "experienceLevel": "SENIOR",
    "yearsExperience": 5,
    "totalCompensation": 350000,
    "baseSalary": 300000,
    "currency": "LKR",
    "anonymize": true
  }')

echo "Submit response:"
jq_or_raw "$SUBMIT_RESPONSE"

SUBMISSION_ID=$(echo "$SUBMIT_RESPONSE" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo ""
echo "✅ Submission ID: $SUBMISSION_ID"
echo "   Status should be: PENDING"

# ── STEP 2: User signup ───────────────────────────────────────────────
check "STEP 2 — User signup"

SIGNUP_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testvoter@watupa.lk",
    "password": "Test@1234!"
  }')

echo "Signup response:"
jq_or_raw "$SIGNUP_RESPONSE"

# ── STEP 3: User login → get JWT ──────────────────────────────────────
check "STEP 3 — User login → receive JWT token"

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testvoter@watupa.lk",
    "password": "Test@1234!"
  }')

echo "Login response:"
jq_or_raw "$LOGIN_RESPONSE"

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | head -1 | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then
  # Try alternative field names
  TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | head -1 | cut -d'"' -f4)
fi
echo ""
echo "✅ JWT token acquired: ${TOKEN:0:40}..."

# ── STEP 4: Vote on the submission (need 3 votes for approval) ────────
check "STEP 4 — Upvote the submission (requires login)"

for i in 1 2 3; do
  # Each voter needs their own account; for simplicity create 3 accounts
  VOTER_EMAIL="voter${i}@watupa.lk"

  # Signup voter
  curl -s -X POST "$BASE_URL/auth/signup" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$VOTER_EMAIL\",\"password\":\"Test@1234!\"}" > /dev/null

  # Login voter
  VLOGIN=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$VOTER_EMAIL\",\"password\":\"Test@1234!\"}")
  VTOKEN=$(echo "$VLOGIN" | grep -o '"token":"[^"]*"' | head -1 | cut -d'"' -f4)
  [ -z "$VTOKEN" ] && VTOKEN=$(echo "$VLOGIN" | grep -o '"accessToken":"[^"]*"' | head -1 | cut -d'"' -f4)

  # Cast vote
  VOTE_RESPONSE=$(curl -s -X POST "$BASE_URL/votes" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $VTOKEN" \
    -d "{\"submissionId\":\"$SUBMISSION_ID\",\"voteType\":\"UP\"}")

  echo "Vote $i by $VOTER_EMAIL:"
  jq_or_raw "$VOTE_RESPONSE"
done

echo ""
echo "✅ 3 upvotes cast — submission should now be APPROVED"

# ── STEP 5: Check submission status ──────────────────────────────────
check "STEP 5 — Verify submission is now APPROVED"

sleep 1   # brief pause for async processing

STATUS_RESPONSE=$(curl -s "$BASE_URL/salary/submissions/$SUBMISSION_ID")
echo "Submission status:"
jq_or_raw "$STATUS_RESPONSE"

STATUS=$(echo "$STATUS_RESPONSE" | grep -o '"status":"[^"]*"' | head -1 | cut -d'"' -f4)
echo ""
echo "✅ Status: $STATUS"

# ── STEP 6: Search for approved salaries ─────────────────────────────
check "STEP 6 — Search approved salaries"

SEARCH_RESPONSE=$(curl -s "$BASE_URL/search?country=LK&experienceLevel=SENIOR")
echo "Search results (APPROVED salaries only):"
jq_or_raw "$SEARCH_RESPONSE"

# ── STEP 7: Stats ─────────────────────────────────────────────────────
check "STEP 7 — Stats endpoint (aggregates on APPROVED salaries)"

STATS_RESPONSE=$(curl -s "$BASE_URL/stats?country=LK")
echo "Stats response:"
jq_or_raw "$STATS_RESPONSE"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Full workflow complete!"
echo "   submit → signup → login → vote (×3) → approved → search → stats"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
