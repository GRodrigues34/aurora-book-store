#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cleanup() {
    echo ""
    echo "Encerrando o Spring Boot e parando os Dockers..."
    cd "$SCRIPT_DIR"
    docker compose down
    exit 0
}

trap cleanup SIGINT SIGTERM

echo "Subindo os Dockers (MySQL/ChromaDB)..."
cd "$SCRIPT_DIR"
docker compose up -d

echo "Compilando o backend..."
cd "$SCRIPT_DIR/aurora-backend"
mvn clean install -DskipTests

echo "Aguardando o MySQL estar pronto na porta 3307..."
until docker exec aurora_mysql mysqladmin ping -h"localhost" -u"root" -p"root" &>/dev/null; do
    echo -n "."
    sleep 1
done
echo " MySQL está pronto!"

echo "Iniciando a Simulação do Boreal Chat..."
mvn spring-boot:run -Dspring-boot.run.profiles=simulation
