@echo off
REM Compila e executa o Sistema de Pedidos (LP2) no Windows.
REM Requisito: Java 17+ instalado. As dependencias ja estao em lib\, sem Maven.
setlocal
cd /d "%~dp0"

echo Compilando...
if not exist target\classes mkdir target\classes
dir /s /b src\*.java > .sources.txt
javac -d target\classes -cp "lib\*" @.sources.txt
del .sources.txt

echo Executando...
java -cp "target\classes;lib\*" br.com.loja.Main
endlocal
