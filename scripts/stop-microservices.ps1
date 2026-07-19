$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot

Push-Location $root
try {
    docker compose -f docker-compose.microservices.yml down | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "Docker Compose no pudo detener correctamente la arquitectura."
    }
} finally {
    Pop-Location
}

Write-Host "Docker Compose detenido."
