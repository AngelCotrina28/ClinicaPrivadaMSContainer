[CmdletBinding()]
param(
    [switch]$PassThru
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$examplePath = Join-Path $root ".env.example"
$envPath = Join-Path $root ".env"

$requiredKeys = @(
    "MYSQL_AUTH_PASSWORD",
    "MYSQL_AUTH_ROOT_PASSWORD",
    "MYSQL_CAJA_PASSWORD",
    "MYSQL_CAJA_ROOT_PASSWORD",
    "POSTGRES_ATENCION_PASSWORD",
    "POSTGRES_NOTIFICACIONES_PASSWORD",
    "MONGO_CITAS_PASSWORD",
    "MONGO_LEGACY_TIMEZONE",
    "AUTH_ADMIN_USERNAME",
    "SYSTEM_ADMIN_PASSWORD",
    "AUTH_ADMIN_EMAIL",
    "AUTH_ADMIN_NAME",
    "AUTH_SEED_ENABLED",
    "DEMO_DATA_ENABLED",
    "DEMO_USER_PASSWORD",
    "JWT_SECRET",
    "CORS_ALLOWED_ORIGINS",
    "FRONTEND_ORIGIN",
    "BACKEND_URL",
    "CITAS_LEGACY_ROUTE_ENABLED",
    "ATENCION_LEGACY_ROUTE_ENABLED",
    "CAJA_LEGACY_ROUTE_ENABLED",
    "GATEWAY_CONNECT_TIMEOUT_SECONDS",
    "GATEWAY_REQUEST_TIMEOUT_SECONDS",
    "MONGODB_HEALTH_ENABLED"
)

if (-not (Test-Path -LiteralPath $examplePath)) {
    throw "Falta el archivo versionado .env.example en la raiz del proyecto."
}

if (-not (Test-Path -LiteralPath $envPath)) {
    Copy-Item -LiteralPath $examplePath -Destination $envPath
    Write-Host "Se creo .env con la configuracion segura de desarrollo local."
}

$values = @{}
foreach ($line in Get-Content -LiteralPath $envPath) {
    if ($line -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)\s*$') {
        $key = $matches[1]
        $value = $matches[2].Trim()
        if ($value.Length -ge 2 -and (
                ($value.StartsWith('"') -and $value.EndsWith('"')) -or
                ($value.StartsWith("'") -and $value.EndsWith("'")))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        $values[$key] = $value
    }
}

$missingKeys = @($requiredKeys | Where-Object {
        -not $values.ContainsKey($_) -or [string]::IsNullOrWhiteSpace($values[$_])
    })
if ($missingKeys.Count -gt 0) {
    throw "Faltan variables obligatorias en .env: $($missingKeys -join ', ')"
}

try {
    $jwtBytes = [Convert]::FromBase64String($values["JWT_SECRET"])
} catch {
    throw "JWT_SECRET debe ser una cadena Base64 valida."
}

if ($jwtBytes.Length -lt 32) {
    throw "JWT_SECRET debe representar al menos 32 bytes."
}

foreach ($timeoutKey in @("GATEWAY_CONNECT_TIMEOUT_SECONDS", "GATEWAY_REQUEST_TIMEOUT_SECONDS")) {
    $timeoutValue = 0
    if (-not [int]::TryParse($values[$timeoutKey], [ref]$timeoutValue) -or $timeoutValue -le 0) {
        throw "$timeoutKey debe ser un numero entero mayor que cero."
    }
}

Write-Host "Configuracion local validada sin mostrar valores sensibles."

if ($PassThru) {
    Write-Output -NoEnumerate $values
}
