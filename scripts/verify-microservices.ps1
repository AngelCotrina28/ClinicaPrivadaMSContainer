$ErrorActionPreference = "Stop"

$adminUsername = if ($env:AUTH_ADMIN_USERNAME) { $env:AUTH_ADMIN_USERNAME } else { "admin" }
$adminPassword = $env:AUTH_ADMIN_PASSWORD

if (-not $adminPassword) {
    throw "Define AUTH_ADMIN_PASSWORD en tu sesion antes de validar. Ejemplo: `$env:AUTH_ADMIN_PASSWORD='tu-password-admin'"
}

function Invoke-Health($name, $url) {
    try {
        $response = Invoke-RestMethod -Uri $url -TimeoutSec 5
        Write-Host "$name health: $($response.status)"
    } catch {
        Write-Host "$name health: DOWN"
        throw
    }
}

Invoke-Health "Gateway" "http://localhost:8090/actuator/health"
Invoke-Health "Auth" "http://localhost:8091/actuator/health"
Invoke-Health "Notificaciones" "http://localhost:8092/actuator/health"
Invoke-Health "Citas" "http://localhost:8093/actuator/health"

$loginBody = @{ username = $adminUsername; password = $adminPassword } | ConvertTo-Json
$login = Invoke-RestMethod `
    -Uri "http://localhost:8090/api/auth/login" `
    -Method Post `
    -Body $loginBody `
    -ContentType "application/json" `
    -TimeoutSec 10

$headers = @{ Authorization = "Bearer $($login.token)" }
Write-Host "Login por Gateway: OK ($($login.username) / $($login.rol))"

$notifBody = @{
    destinatarioTipo = "PACIENTE"
    destinatarioId = 1
    tipo = "CITA"
    canal = "APP"
    titulo = "Cita confirmada"
    mensaje = "Tu cita fue registrada correctamente."
    referenciaTipo = "CITA"
    referenciaId = 1001
} | ConvertTo-Json

$notificacion = Invoke-RestMethod `
    -Uri "http://localhost:8090/api/notificaciones" `
    -Method Post `
    -Body $notifBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Notificacion creada por Gateway: ID=$($notificacion.id)"

$citaBody = @{
    pacienteId = 1
    medicoId = 10
    especialidadId = 2
    fecha = "2026-07-05"
    horaInicio = "10:00"
    horaFin = "10:30"
    consultorio = "Consultorio 201"
    motivo = "Consulta general"
} | ConvertTo-Json

$cita = Invoke-RestMethod `
    -Uri "http://localhost:8090/api/citas" `
    -Method Post `
    -Body $citaBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Cita creada por Gateway: ID=$($cita.id)"

try {
    Invoke-RestMethod `
        -Uri "http://localhost:8090/api/citas" `
        -Method Post `
        -Body $citaBody `
        -ContentType "application/json" `
        -Headers $headers `
        -TimeoutSec 10 | Out-Null
    throw "El cruce de horario no fue bloqueado."
} catch {
    if ($_.Exception.Message -eq "El cruce de horario no fue bloqueado.") {
        throw
    }
    Write-Host "Cruce de cita bloqueado: OK"
}

$cancelBody = @{ motivoCancelacion = "Prueba de microservicios" } | ConvertTo-Json
$cancelada = Invoke-RestMethod `
    -Uri "http://localhost:8090/api/citas/$($cita.id)/cancelar" `
    -Method Patch `
    -Body $cancelBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Cita cancelada: $($cancelada.estado)"
Write-Host "Validacion completa: OK"
