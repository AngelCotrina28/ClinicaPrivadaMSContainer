$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$initializeEnv = Join-Path $PSScriptRoot "initialize-local-env.ps1"

& $initializeEnv | Out-Null

Write-Host "Levantando arquitectura de microservicios con Docker Compose..."
Push-Location $root
try {
    $seedServices = @(
        "seed-mysql-auth",
        "seed-mysql-caja",
        "seed-postgres-atencion",
        "seed-postgres-notificaciones",
        "seed-mongo-citas"
    )

    # Los contenedores one-shot quedan detenidos despues de una ejecucion correcta.
    # Se recrean para que una nueva version de las semillas tambien migre volumenes existentes.
    docker compose -f docker-compose.microservices.yml rm -f @seedServices | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudieron preparar los contenedores de semilla para una nueva ejecucion."
    }

    docker compose -f docker-compose.microservices.yml up -d --build | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "Docker Compose no pudo iniciar toda la arquitectura (exit code $LASTEXITCODE)."
    }
} finally {
    Pop-Location
}

Write-Host "Listo. Panel Eureka: http://localhost:8761"
Write-Host "Config Server: http://localhost:8888/clinica-gateway-service/default"
Write-Host "Gateway: http://localhost:8090"
Write-Host "Ejecuta scripts\verify-microservices.ps1 para validar el flujo."
