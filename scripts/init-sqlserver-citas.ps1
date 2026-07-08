$ErrorActionPreference = "Stop"

$containerName = "clinica-sqlserver-citas"
$password = if ($env:MSSQL_SA_PASSWORD) { $env:MSSQL_SA_PASSWORD } else { "ClinicaSqlServerLocal123!" }
$database = "clinica_citas_db"
$maxAttempts = 30

for ($attempt = 1; $attempt -le $maxAttempts; $attempt++) {
    $result = docker exec $containerName /opt/mssql-tools18/bin/sqlcmd `
        -S localhost `
        -U sa `
        -P $password `
        -C `
        -Q "SELECT 1" 2>$null

    if ($LASTEXITCODE -eq 0) {
        break
    }

    if ($attempt -eq $maxAttempts) {
        throw "SQL Server no estuvo listo despues de $maxAttempts intentos."
    }

    Start-Sleep -Seconds 3
}

$query = "IF DB_ID('$database') IS NULL CREATE DATABASE [$database];"
docker exec $containerName /opt/mssql-tools18/bin/sqlcmd `
    -S localhost `
    -U sa `
    -P $password `
    -C `
    -Q $query | Out-Host

Write-Host "Base SQL Server lista: $database"
