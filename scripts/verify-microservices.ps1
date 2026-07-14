param(
    [string]$GatewayBaseUrl = "http://localhost:8090",
    [switch]$SkipDirectHealth
)

$ErrorActionPreference = "Stop"

$GatewayBaseUrl = $GatewayBaseUrl.TrimEnd("/")

$adminUsername = if ($env:AUTH_ADMIN_USERNAME) { $env:AUTH_ADMIN_USERNAME } else { "admin" }
$adminPassword = if ($env:AUTH_ADMIN_PASSWORD) { $env:AUTH_ADMIN_PASSWORD } else { "ClinicaAdminLocal123!" }

function Invoke-Health($name, $url) {
    try {
        $response = Invoke-RestMethod -Uri $url -TimeoutSec 5
        Write-Host "$name health: $($response.status)"
    } catch {
        Write-Host "$name health: DOWN"
        throw
    }
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

$loginBody = @{ username = $adminUsername; password = $adminPassword } | ConvertTo-Json
$login = Invoke-RestMethod `
    -Uri "$GatewayBaseUrl/api/auth/login" `
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
    -Uri "$GatewayBaseUrl/api/ms/notificaciones" `
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
