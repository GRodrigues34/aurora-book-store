#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cleanup() {
    echo ""
    echo "Shutting down"
    kill $FRONT_PID 2>/dev/null
    exit
}

trap cleanup SIGINT SIGTERM EXIT

echo "clean docker"
cd "$SCRIPT_DIR"
docker compose down -v
if [ $? -ne 0 ]; then
    echo "docker error"
    exit 1
fi

echo "frontend"
cd "$SCRIPT_DIR/aurora-frontend"
npm run dev &
FRONT_PID=$!

echo "backend"
cd "$SCRIPT_DIR/aurora-backend"
mvn clean install
mvn spring-boot:run
