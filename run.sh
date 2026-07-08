#!/bin/bash


cleanup() {
    echo ""
    echo "Shutting down"
    kill $FRONT_PID 2>/dev/null
    exit
}

trap cleanup SIGINT SIGTERM EXIT

echo "clean docker"
docker compose down -v
if [ $? -ne 0 ]; then
    echo "docker error"
    exit 1
fi

echo "frontend"
cd aurora-frontend
npm run dev &
FRONT_PID=$!
cd ..

echo "backend"
cd aurora-backend
mvn clean install
mvn spring-boot:run
