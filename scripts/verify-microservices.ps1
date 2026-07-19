param(
    [string]$GatewayBaseUrl = "http://localhost:8090",
    [switch]$SkipDirectHealth,
    [switch]$ExerciseWrites
)

$ErrorActionPreference = "Stop"

$localEnv = & (Join-Path $PSScriptRoot "initialize-local-env.ps1") -PassThru
$GatewayBaseUrl = $GatewayBaseUrl.TrimEnd("/")

$adminUsername = if ($env:AUTH_ADMIN_USERNAME) { $env:AUTH_ADMIN_USERNAME } else { $localEnv["AUTH_ADMIN_USERNAME"] }
$adminPassword = if ($env:SYSTEM_ADMIN_PASSWORD) { $env:SYSTEM_ADMIN_PASSWORD } else { $localEnv["SYSTEM_ADMIN_PASSWORD"] }

function Invoke-Health($name, $url, [int]$Attempts = 12) {
    $lastError = $null
    for ($attempt = 1; $attempt -le $Attempts; $attempt++) {
        try {
            $response = Invoke-RestMethod -Uri $url -TimeoutSec 5
            if ($response.status -eq "UP") {
                Write-Host "$name health: UP"
                return
            }
            $lastError = "estado $($response.status)"
        } catch {
            $lastError = $_.Exception.Message
        }

        if ($attempt -lt $Attempts) {
            Start-Sleep -Seconds 5
        }
    }

    Write-Host "$name health: DOWN"
    throw "$name no quedo saludable tras $Attempts intentos. Ultimo error: $lastError"
}

function Assert-MinimumCount($name, $items, [int]$minimum) {
    $count = @($items).Count
    if ($count -lt $minimum) {
        throw "$name no contiene los datos demo esperados. Minimo=$minimum Actual=$count"
    }
    Write-Host "$name sembrados: $count (minimo $minimum)"
}

if ($SkipDirectHealth) {
    Invoke-Health "Gateway" "$GatewayBaseUrl/actuator/health"
} else {
    Invoke-Health "Config Server" "http://localhost:8888/actuator/health"
    Invoke-Health "Eureka" "http://localhost:8761/actuator/health"
    Invoke-Health "Gateway" "$GatewayBaseUrl/actuator/health"
    Invoke-Health "Auth" "http://localhost:8091/actuator/health"
    Invoke-Health "Notificaciones" "http://localhost:8092/actuator/health"
    Invoke-Health "Citas" "http://localhost:8093/actuator/health"
    Invoke-Health "Atencion Medica" "http://localhost:8094/actuator/health"
    Invoke-Health "Caja Facturacion" "http://localhost:8095/actuator/health"
}

$preflightHeaders = @{
    Origin = "http://localhost:4200"
    "Access-Control-Request-Method" = "POST"
    "Access-Control-Request-Headers" = "authorization,content-type"
}
$preflight = Invoke-WebRequest `
    -Uri "$GatewayBaseUrl/api/auth/login" `
    -Method Options `
    -Headers $preflightHeaders `
    -UseBasicParsing `
    -TimeoutSec 10
if ($preflight.Headers["Access-Control-Allow-Origin"] -ne "http://localhost:4200") {
    throw "El Gateway no devolvio el encabezado CORS esperado para el frontend local."
}
Write-Host "Preflight CORS por Gateway: OK"

$loginBody = @{ username = $adminUsername; password = $adminPassword } | ConvertTo-Json
$login = Invoke-RestMethod `
    -Uri "$GatewayBaseUrl/api/auth/login" `
    -Method Post `
    -Body $loginBody `
    -ContentType "application/json" `
    -TimeoutSec 10

$headers = @{ Authorization = "Bearer $($login.token)" }
Write-Host "Login por Gateway: OK ($($login.username) / $($login.rol))"

$roles = Invoke-RestMethod -Uri "$GatewayBaseUrl/api/ms/auth/roles" -Headers $headers -TimeoutSec 10
$usuarios = Invoke-RestMethod -Uri "$GatewayBaseUrl/api/ms/auth/usuarios" -Headers $headers -TimeoutSec 10
$notificacionesDemo = Invoke-RestMethod -Uri "$GatewayBaseUrl/api/ms/notificaciones" -Headers $headers -TimeoutSec 10
$citasDemoPaciente = Invoke-RestMethod -Uri "$GatewayBaseUrl/api/ms/citas/paciente/1" -Headers $headers -TimeoutSec 10
$atencionesDemoPaciente = Invoke-RestMethod -Uri "$GatewayBaseUrl/api/ms/atenciones/paciente/1" -Headers $headers -TimeoutSec 10
$deudasDemo = Invoke-RestMethod -Uri "$GatewayBaseUrl/api/ms/caja/deudas" -Headers $headers -TimeoutSec 10
$pagosDemoPaciente = Invoke-RestMethod -Uri "$GatewayBaseUrl/api/ms/caja/pagos/paciente/1" -Headers $headers -TimeoutSec 10

Assert-MinimumCount "Roles" $roles 7
Assert-MinimumCount "Usuarios Auth" $usuarios 7
Assert-MinimumCount "Notificaciones" $notificacionesDemo 6
Assert-MinimumCount "Citas del paciente demo 1" $citasDemoPaciente 3
Assert-MinimumCount "Atenciones del paciente demo 1" $atencionesDemoPaciente 1
Assert-MinimumCount "Deudas" $deudasDemo 5
Assert-MinimumCount "Pagos del paciente demo 1" $pagosDemoPaciente 2

$dashboard = Invoke-RestMethod `
    -Uri "$GatewayBaseUrl/api/dashboard/resumen" `
    -Headers $headers `
    -TimeoutSec 15
if ($null -eq $dashboard) {
    throw "El backend principal no devolvio el resumen del dashboard."
}
Write-Host "JWT compartido y fallback al backend principal: OK"

if (-not $ExerciseWrites) {
    Write-Host "Validacion de solo lectura completa: OK"
    Write-Host "Usa -ExerciseWrites solo si deseas crear datos transitorios y probar POST/PATCH."
    return
}

Write-Warning "ExerciseWrites esta activo: se crearan notificacion, cita, atencion, deuda y pago de prueba."

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
    -Uri "$GatewayBaseUrl/api/ms/notificaciones" `
    -Method Post `
    -Body $notifBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Notificacion creada por Gateway: ID=$($notificacion.id)"

$testDate = (Get-Date).Date.AddDays(30).ToString("yyyy-MM-dd")
$citaBody = @{
    pacienteId = 1
    medicoId = 10
    especialidadId = 2
    fecha = $testDate
    horaInicio = "10:00"
    horaFin = "10:30"
    consultorio = "Consultorio 201"
    motivo = "Consulta general"
} | ConvertTo-Json

$cita = Invoke-RestMethod `
    -Uri "$GatewayBaseUrl/api/ms/citas" `
    -Method Post `
    -Body $citaBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Cita creada por Gateway: ID=$($cita.id)"

$atencionBody = @{
    historiaClinicaId = 1
    pacienteId = 1
    medicoId = 10
    citaId = "$($cita.id)"
    motivoConsulta = "Dolor abdominal"
    diagnostico = "Gastritis probable"
    tratamiento = "Control dietario y medicacion indicada"
    indicacionesReceta = "Tomar medicamento despues de alimentos"
} | ConvertTo-Json

$atencion = Invoke-RestMethod `
    -Uri "$GatewayBaseUrl/api/ms/atenciones" `
    -Method Post `
    -Body $atencionBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Atencion medica registrada por Gateway: ID=$($atencion.id)"

$cerrarAtencionBody = @{
    tratamiento = "Tratamiento final registrado"
    indicacionesReceta = "Reposo relativo y control en 7 dias"
} | ConvertTo-Json

$atencionCerrada = Invoke-RestMethod `
    -Uri "$GatewayBaseUrl/api/ms/atenciones/$($atencion.id)/cerrar" `
    -Method Patch `
    -Body $cerrarAtencionBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Atencion medica cerrada: $($atencionCerrada.estado)"

$deudaBody = @{
    pacienteId = 1
    concepto = "Consulta medica"
    referenciaTipo = "ATENCION"
    referenciaId = "$($atencion.id)"
    montoTotal = 80.00
} | ConvertTo-Json

$deuda = Invoke-RestMethod `
    -Uri "$GatewayBaseUrl/api/ms/caja/deudas" `
    -Method Post `
    -Body $deudaBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Deuda creada por Gateway: ID=$($deuda.id) Saldo=$($deuda.saldo)"

$pagoBody = @{
    deudaId = $deuda.id
    monto = 80.00
    metodoPago = "EFECTIVO"
    observacion = "Pago de prueba de microservicios"
} | ConvertTo-Json

$pago = Invoke-RestMethod `
    -Uri "$GatewayBaseUrl/api/ms/caja/pagos" `
    -Method Post `
    -Body $pagoBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Pago registrado por Gateway: ID=$($pago.id) Comprobante=$($pago.numeroComprobante)"

try {
    Invoke-RestMethod `
        -Uri "$GatewayBaseUrl/api/ms/citas" `
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
    -Uri "$GatewayBaseUrl/api/ms/citas/$($cita.id)/cancelar" `
    -Method Patch `
    -Body $cancelBody `
    -ContentType "application/json" `
    -Headers $headers `
    -TimeoutSec 10

Write-Host "Cita cancelada: $($cancelada.estado)"
Write-Host "Validacion completa: OK"
