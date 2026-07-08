$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot

Write-Host "Levantando arquitectura de microservicios con Docker Compose..."
Push-Location $root
try {
    docker compose -f docker-compose.microservices.yml up -d --build | Out-Host
} finally {
    Pop-Location
}

Write-Host "Listo. Panel Eureka: http://localhost:8761"
Write-Host "Config Server: http://localhost:8888/clinica-gateway-service/default"
Write-Host "Gateway: http://localhost:8090"
Write-Host "Ejecuta scripts\verify-microservices.ps1 para validar el flujo."
