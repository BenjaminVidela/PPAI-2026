# Script para compilar y ejecutar el backend de CU36
# Descarga Maven automaticamente si no esta instalado

$MAVEN_VERSION = "3.9.6"
$MAVEN_DIR = "$PSScriptRoot\.maven\apache-maven-$MAVEN_VERSION"
$MVN = "$MAVEN_DIR\bin\mvn.cmd"

if (-not (Test-Path $MVN)) {
    Write-Host "Maven no encontrado. Descargando Maven $MAVEN_VERSION..." -ForegroundColor Yellow
    $url = "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
    $zip = "$PSScriptRoot\.maven\maven.zip"
    New-Item -ItemType Directory -Force "$PSScriptRoot\.maven" | Out-Null
    Invoke-WebRequest -Uri $url -OutFile $zip -UseBasicParsing
    Write-Host "Descomprimiendo..." -ForegroundColor Yellow
    Expand-Archive -Path $zip -DestinationPath "$PSScriptRoot\.maven" -Force
    Remove-Item $zip
    Write-Host "Maven instalado correctamente." -ForegroundColor Green
}

Write-Host "Iniciando backend Spring Boot en puerto 8080..." -ForegroundColor Cyan
Write-Host "Presione Ctrl+C para detener." -ForegroundColor Gray
& $MVN spring-boot:run "-f" "$PSScriptRoot\pom.xml"
