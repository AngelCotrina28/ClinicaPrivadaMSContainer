$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot

Write-Host "Levantando gestores de BD para microservicios: PostgreSQL y SQL Server..."
Push-Location $root
try {
    docker compose -f docker-compose.microservices.yml up -d | Out-Host
} finally {
    Pop-Location
}

Start-Sleep -Seconds 5

& (Join-Path $PSScriptRoot "init-sqlserver-citas.ps1")

$services = @(
    @{ Name = "Auth"; Path = "Clinica-Auth-Service"; Jar = "auth-service-0.0.1-SNAPSHOT.jar"; Port = 8091; PidFile = ".auth-service.pid"; Log = "auth-service.out.log"; Err = "auth-service.err.log" },
    @{ Name = "Notificaciones"; Path = "Clinica-Notificaciones-Service"; Jar = "notificaciones-service-0.0.1-SNAPSHOT.jar"; Port = 8092; PidFile = ".notificaciones-service.pid"; Log = "notificaciones-service.out.log"; Err = "notificaciones-service.err.log" },
    @{ Name = "Citas"; Path = "Clinica-Citas-Service"; Jar = "citas-service-0.0.1-SNAPSHOT.jar"; Port = 8093; PidFile = ".citas-service.pid"; Log = "citas-service.out.log"; Err = "citas-service.err.log" },
    @{ Name = "Gateway"; Path = "Clinica-Gateway-Service"; Jar = "gateway-service-0.0.1-SNAPSHOT.jar"; Port = 8090; PidFile = ".gateway-service.pid"; Log = "gateway-service.out.log"; Err = "gateway-service.err.log" }
)

function Build-Service($service) {
    $dir = Join-Path $root $service.Path
    Push-Location $dir
    try {
        Write-Host "Compilando $($service.Name)..."
        & .\mvnw.cmd -DskipTests package | Out-Host
    } finally {
        Pop-Location
    }
}

function Start-ServiceJar($service) {
    $dir = Join-Path $root $service.Path
    $jarPath = Join-Path $dir "target\$($service.Jar)"
    $pidPath = Join-Path $dir $service.PidFile
    $logPath = Join-Path $dir $service.Log
    $errPath = Join-Path $dir $service.Err

    if (-not (Test-Path $jarPath)) {
        throw "No existe el jar esperado: $jarPath"
    }

    $existing = Get-NetTCPConnection -LocalPort $service.Port -ErrorAction SilentlyContinue
    if ($existing) {
        Write-Host "$($service.Name) parece estar usando el puerto $($service.Port); no se inicia otro proceso."
        return
    }

    Remove-Item -Force $logPath, $errPath -ErrorAction SilentlyContinue
    $process = Start-Process -FilePath "java" `
        -ArgumentList @("-jar", $jarPath) `
        -WorkingDirectory $dir `
        -RedirectStandardOutput $logPath `
        -RedirectStandardError $errPath `
        -WindowStyle Hidden `
        -PassThru

    Set-Content -Path $pidPath -Value $process.Id
    Write-Host "$($service.Name) iniciado en puerto $($service.Port). PID=$($process.Id)"
}

foreach ($service in $services) {
    Build-Service $service
}

foreach ($service in $services) {
    Start-ServiceJar $service
}

Write-Host "Listo. Ejecuta scripts\verify-microservices.ps1 para validar el flujo."
