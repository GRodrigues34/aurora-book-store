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

echo "Aguardando o MySQL estar pronto na porta 3307..."
until docker exec aurora_mysql mysqladmin ping -h"localhost" -u"root" -p"root" &>/dev/null; do
    echo -n "."
    sleep 1
done
echo " MySQL está pronto!"

echo "Iniciando a Simulação do Boreal Chat..."
mvn spring-boot:run -Dspring-boot.run.profiles=simulation
