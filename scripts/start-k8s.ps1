param(
    [string]$Context = "docker-desktop",
    [string]$Namespace = "clinica-ms",
    [switch]$RebuildImages
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$initializeEnv = Join-Path $PSScriptRoot "initialize-local-env.ps1"
$localEnv = & $initializeEnv -PassThru

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
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo aplicar $file."
    }
}

function Apply-GeneratedResource([string[]]$KubectlArguments, [string]$Description) {
    $yaml = @(& kubectl @KubectlArguments)
    if ($LASTEXITCODE -ne 0 -or $yaml.Count -eq 0) {
        throw "No se pudo generar $Description."
    }

    $yaml | kubectl apply -f - | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo aplicar $Description."
    }
}

function Sync-LocalConfiguration {
    Write-Host "Sincronizando ConfigMap y Secret desde .env..."

    $configArguments = @(
        "-n", $Namespace, "create", "configmap", "clinica-ms-shared-config",
        "--from-literal=SPRING_CONFIG_IMPORT=optional:configserver:http://config-server:8888",
        "--from-literal=EUREKA_DEFAULT_ZONE=http://eureka-server:8761/eureka/",
        "--from-literal=CORS_ALLOWED_ORIGINS=$($localEnv['CORS_ALLOWED_ORIGINS'])",
        "--from-literal=FRONTEND_ORIGIN=$($localEnv['FRONTEND_ORIGIN'])",
        "--from-literal=BACKEND_URL=$($localEnv['BACKEND_URL'])",
        "--from-literal=CITAS_LEGACY_ROUTE_ENABLED=$($localEnv['CITAS_LEGACY_ROUTE_ENABLED'])",
        "--from-literal=ATENCION_LEGACY_ROUTE_ENABLED=$($localEnv['ATENCION_LEGACY_ROUTE_ENABLED'])",
        "--from-literal=CAJA_LEGACY_ROUTE_ENABLED=$($localEnv['CAJA_LEGACY_ROUTE_ENABLED'])",
        "--from-literal=AUTH_ADMIN_USERNAME=$($localEnv['AUTH_ADMIN_USERNAME'])",
        "--from-literal=AUTH_ADMIN_EMAIL=$($localEnv['AUTH_ADMIN_EMAIL'])",
        "--from-literal=AUTH_ADMIN_NAME=$($localEnv['AUTH_ADMIN_NAME'])",
        "--from-literal=AUTH_SEED_ENABLED=$($localEnv['AUTH_SEED_ENABLED'])",
        "--from-literal=DEMO_DATA_ENABLED=$($localEnv['DEMO_DATA_ENABLED'])",
        "--from-literal=MONGODB_HEALTH_ENABLED=$($localEnv['MONGODB_HEALTH_ENABLED'])",
        "--from-literal=MONGO_LEGACY_TIMEZONE=$($localEnv['MONGO_LEGACY_TIMEZONE'])",
        "--from-literal=GATEWAY_CONNECT_TIMEOUT_SECONDS=$($localEnv['GATEWAY_CONNECT_TIMEOUT_SECONDS'])",
        "--from-literal=GATEWAY_REQUEST_TIMEOUT_SECONDS=$($localEnv['GATEWAY_REQUEST_TIMEOUT_SECONDS'])",
        "--dry-run=client", "-o", "yaml"
    )
    Apply-GeneratedResource $configArguments "ConfigMap clinica-ms-shared-config"

    $secretArguments = @(
        "-n", $Namespace, "create", "secret", "generic", "clinica-ms-secrets",
        "--from-literal=mysql-auth-password=$($localEnv['MYSQL_AUTH_PASSWORD'])",
        "--from-literal=mysql-auth-root-password=$($localEnv['MYSQL_AUTH_ROOT_PASSWORD'])",
        "--from-literal=mysql-caja-password=$($localEnv['MYSQL_CAJA_PASSWORD'])",
        "--from-literal=mysql-caja-root-password=$($localEnv['MYSQL_CAJA_ROOT_PASSWORD'])",
        "--from-literal=postgres-atencion-password=$($localEnv['POSTGRES_ATENCION_PASSWORD'])",
        "--from-literal=postgres-notificaciones-password=$($localEnv['POSTGRES_NOTIFICACIONES_PASSWORD'])",
        "--from-literal=mongo-citas-password=$($localEnv['MONGO_CITAS_PASSWORD'])",
        "--from-literal=auth-admin-password=$($localEnv['SYSTEM_ADMIN_PASSWORD'])",
        "--from-literal=demo-user-password=$($localEnv['DEMO_USER_PASSWORD'])",
        "--from-literal=jwt-secret=$($localEnv['JWT_SECRET'])",
        "--dry-run=client", "-o", "yaml"
    )
    Apply-GeneratedResource $secretArguments "Secret clinica-ms-secrets"
}

function Sync-SeedConfigMaps {
    Write-Host "Empaquetando los scripts versionados de base de datos..."
    $seedConfigMaps = @(
        @{
            Name = "seed-mysql-auth"
            Files = @(
                "schema.sql=$(Join-Path $root 'database\mysql\auth\schema.sql')",
                "data.sql=$(Join-Path $root 'database\mysql\auth\data.sql')"
            )
        },
        @{
            Name = "seed-mysql-caja"
            Files = @(
                "schema.sql=$(Join-Path $root 'database\mysql\caja\schema.sql')",
                "data.sql=$(Join-Path $root 'database\mysql\caja\data.sql')"
            )
        },
        @{
            Name = "seed-postgres-atencion"
            Files = @(
                "schema.sql=$(Join-Path $root 'database\postgresql\atencion\schema.sql')",
                "data.sql=$(Join-Path $root 'database\postgresql\atencion\data.sql')"
            )
        },
        @{
            Name = "seed-postgres-notificaciones"
            Files = @(
                "schema.sql=$(Join-Path $root 'database\postgresql\notificaciones\schema.sql')",
                "data.sql=$(Join-Path $root 'database\postgresql\notificaciones\data.sql')"
            )
        },
        @{
            Name = "seed-mongo-citas"
            Files = @(
                "schema.mongodb.js=$(Join-Path $root 'database\mongodb\citas\schema.mongodb.js')",
                "data.mongodb.js=$(Join-Path $root 'database\mongodb\citas\data.mongodb.js')"
            )
        }
    )

    foreach ($seedConfigMap in $seedConfigMaps) {
        $arguments = @("-n", $Namespace, "create", "configmap", $seedConfigMap.Name)
        foreach ($file in $seedConfigMap.Files) {
            $arguments += "--from-file=$file"
        }
        $arguments += @("--dry-run=client", "-o", "yaml")
        Apply-GeneratedResource $arguments "ConfigMap $($seedConfigMap.Name)"
    }
}

function Run-DatabaseSeeds {
    $seedJobs = @(
        "seed-mysql-auth",
        "seed-mysql-caja",
        "seed-postgres-atencion",
        "seed-postgres-notificaciones",
        "seed-mongo-citas"
    )

    Write-Host "Ejecutando semillas idempotentes de base de datos..."
    foreach ($job in $seedJobs) {
        kubectl -n $Namespace delete job $job --ignore-not-found=true | Out-Host
        if ($LASTEXITCODE -ne 0) {
            throw "No se pudo preparar job/$job para una nueva ejecucion."
        }
    }

    Apply-Manifest "k8s/02-database-seeds.yaml"
    foreach ($job in $seedJobs) {
        Write-Host "Esperando job/$job..."
        kubectl -n $Namespace wait --for=condition=complete "job/$job" --timeout=10m | Out-Host
        if ($LASTEXITCODE -ne 0) {
            throw "La semilla job/$job no finalizo correctamente."
        }
    }
}

function Wait-Deployment($deployment) {
    Write-Host "Esperando deployment/$deployment..."
    kubectl -n $Namespace rollout status "deployment/$deployment" --timeout=10m | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "deployment/$deployment no quedo disponible."
    }
}

function Restart-Deployment($deployment) {
    kubectl -n $Namespace rollout restart "deployment/$deployment" | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo reiniciar deployment/$deployment con la configuracion actual."
    }
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
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo activar el contexto Kubernetes '$Context'."
    }

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
        if ($LASTEXITCODE -ne 0) {
            throw "No se pudieron construir todas las imagenes locales para Kubernetes."
        }
    } else {
        Write-Host "Imagenes locales encontradas. No se reconstruyen."
    }

    Write-Host "Aplicando manifiestos Kubernetes por etapas..."

    Apply-Manifest "k8s/00-namespace-config.yaml"
    Sync-LocalConfiguration

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

    Sync-SeedConfigMaps
    Run-DatabaseSeeds

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
        Restart-Deployment $deployment
        Wait-Deployment $deployment
    }

    Apply-Manifest "k8s/04-gateway.yaml"
    Restart-Deployment "gateway-service"
    Wait-Deployment "gateway-service"

    Write-Host "Kubernetes listo."
    Write-Host "Gateway: ejecuta scripts\verify-k8s.ps1 para validar el flujo por port-forward."
    Write-Host "Panel Eureka: kubectl -n $Namespace port-forward svc/eureka-server 8761:8761"
} finally {
    Pop-Location
}
