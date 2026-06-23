#!/usr/bin/env bash
# Compila e executa o Sistema de Pedidos (LP2).
# Requisito: Java 17+ instalado (testado com Java 21). As dependências (sqlite-jdbc)
# já acompanham o projeto na pasta lib/, então NÃO é necessário Maven nem internet.
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

echo "Compilando..."
find src -name '*.java' > .sources.txt
mkdir -p target/classes
javac -d target/classes -cp "lib/*" @.sources.txt
rm -f .sources.txt

echo "Executando..."
java -cp "target/classes:lib/*" br.com.loja.Main
