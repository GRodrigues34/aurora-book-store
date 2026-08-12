#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Iniciando a bateria de testes automatizados do backend..."
cd "$SCRIPT_DIR/aurora-backend"
mvn clean test
