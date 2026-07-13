#!/bin/bash

cleanup() {
    echo ""
    echo "Encerrando o Spring Boot e parando os Dockers..."
    cd /home/gr/aurora-book-store
    docker compose down
    exit 0
}

trap cleanup SIGINT SIGTERM

echo "Subindo os Dockers (MySQL/ChromaDB)..."
cd /home/gr/aurora-book-store
docker compose up -d

echo "Compilando o backend..."
cd /home/gr/aurora-book-store/aurora-backend
mvn clean install -DskipTests

echo "Iniciando o Spring Boot..."
mvn spring-boot:run
