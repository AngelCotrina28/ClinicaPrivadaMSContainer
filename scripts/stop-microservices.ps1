$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot

Push-Location $root
try {
    docker compose -f docker-compose.microservices.yml down | Out-Host
} finally {
    Pop-Location
}

Write-Host "Docker Compose detenido."
