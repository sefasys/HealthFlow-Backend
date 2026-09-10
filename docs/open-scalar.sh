#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PORT=8000
LOG_FILE="/tmp/healthflow-scalar.log"

nohup python3 -m http.server "$PORT" \
  --directory "$SCRIPT_DIR" \
  >"$LOG_FILE" 2>&1 &

sleep 1

xdg-open "http://localhost:$PORT/scalar.html"
