param(
    [string]$Context = "docker-desktop",
    [string]$Namespace = "clinica-ms",
    [switch]$RebuildImages
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot

function Test-CommandExists($name) {
    return [bool](Get-Command $name -ErrorAction SilentlyContinue)
}

function Test-DockerImage($image) {
    docker image inspect $image *> $null
    return $LASTEXITCODE -eq 0
}

function Apply-Manifest($file) {
    Write-Host "Aplicando $file..."
    kubectl apply -f $file | Out-Host
}

function Wait-Deployment($deployment) {
    Write-Host "Esperando deployment/$deployment..."
    kubectl -n $Namespace rollout status "deployment/$deployment" --timeout=10m | Out-Host
}

if (-not (Test-CommandExists "kubectl")) {
    throw "No encuentro kubectl. Instala/habilita Kubernetes desde Docker Desktop."
}

if (-not (Test-CommandExists "docker")) {
    throw "No encuentro docker. Abre Docker Desktop antes de ejecutar este script."
}

Push-Location $root
try {
    $contexts = @(kubectl config get-contexts -o name 2>$null)
    if (-not ($contexts -contains $Context)) {
        throw "No existe el contexto '$Context'. En Docker Desktop habilita: Settings > Kubernetes > Enable Kubernetes, espera que termine y vuelve a ejecutar."
    }

    kubectl config use-context $Context | Out-Host

    $images = @(
        "clinica-config-server:local",
        "clinica-eureka-server:local",
        "clinica-auth-service:local",
        "clinica-notificaciones-service:local",
        "clinica-citas-service:local",
        "clinica-atencion-medica-service:local",
        "clinica-caja-facturacion-service:local",
        "clinica-gateway-service:local"
    )

    $missingImages = @($images | Where-Object { -not (Test-DockerImage $_) })
    if ($RebuildImages -or $missingImages.Count -gt 0) {
        Write-Host "Construyendo imagenes locales para Kubernetes..."
        docker compose -f docker-compose.microservices.yml build | Out-Host
    } else {
        Write-Host "Imagenes locales encontradas. No se reconstruyen."
    }

    Write-Host "Aplicando manifiestos Kubernetes por etapas..."

    Apply-Manifest "k8s/00-namespace-config.yaml"

    Apply-Manifest "k8s/01-databases.yaml"
    $databaseDeployments = @(
        "mysql-auth",
        "mysql-caja",
        "postgres-atencion",
        "postgres-notificaciones",
        "mongo-citas"
    )
    foreach ($deployment in $databaseDeployments) {
        Wait-Deployment $deployment
    }

    Apply-Manifest "k8s/02-config-eureka.yaml"
    $infrastructureDeployments = @(
        "config-server",
        "eureka-server"
    )
    foreach ($deployment in $infrastructureDeployments) {
        Wait-Deployment $deployment
    }

    Apply-Manifest "k8s/03-microservices.yaml"
    $serviceDeployments = @(
        "auth-service",
        "notificaciones-service",
        "citas-service",
        "atencion-medica-service",
        "caja-facturacion-service"
    )
    foreach ($deployment in $serviceDeployments) {
        Wait-Deployment $deployment
    }

    Apply-Manifest "k8s/04-gateway.yaml"
    Wait-Deployment "gateway-service"

    Write-Host "Kubernetes listo."
    Write-Host "Gateway: ejecuta scripts\verify-k8s.ps1 para validar el flujo por port-forward."
    Write-Host "Panel Eureka: kubectl -n $Namespace port-forward svc/eureka-server 8761:8761"
} finally {
    Pop-Location
}
