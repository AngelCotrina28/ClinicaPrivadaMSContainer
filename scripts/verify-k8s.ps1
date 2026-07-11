param(
    [string]$Namespace = "clinica-ms",
    [int]$GatewayPort = 8090
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$gatewayBaseUrl = "http://localhost:$GatewayPort"
$stdoutLog = Join-Path $env:TEMP "clinica-k8s-gateway-port-forward.out.log"
$stderrLog = Join-Path $env:TEMP "clinica-k8s-gateway-port-forward.err.log"
$portForward = $null

Push-Location $root
try {
    Write-Host "Abriendo port-forward del Gateway en $gatewayBaseUrl..."
    $portForward = Start-Process `
        -FilePath "kubectl" `
        -ArgumentList @("-n", $Namespace, "port-forward", "svc/gateway-service", "$GatewayPort`:8090") `
        -PassThru `
        -WindowStyle Hidden `
        -RedirectStandardOutput $stdoutLog `
        -RedirectStandardError $stderrLog

    Start-Sleep -Seconds 5

    if ($portForward.HasExited) {
        $errorText = if (Test-Path $stderrLog) { Get-Content -Raw -Path $stderrLog } else { "" }
        throw "No se pudo abrir el port-forward del Gateway. Detalle: $errorText"
    }

    & (Join-Path $PSScriptRoot "verify-microservices.ps1") `
        -GatewayBaseUrl $gatewayBaseUrl `
        -SkipDirectHealth
} finally {
    if ($portForward -and -not $portForward.HasExited) {
        Stop-Process -Id $portForward.Id -Force
    }
    Pop-Location
}
