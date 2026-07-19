[CmdletBinding()]
param(
    [string]$GatewayBaseUrl = "https://clinica-gateway-service.onrender.com",
    [Parameter(Mandatory = $true)]
    [System.Security.SecureString]$AdminPassword,
    [string]$DemoUserPassword = "ClinicaDemoLocal123!"
)

$ErrorActionPreference = "Stop"
$GatewayBaseUrl = $GatewayBaseUrl.TrimEnd("/")
$script:failures = 0
$headers = @{}

function Invoke-ApiGet([string]$path) {
    return Invoke-RestMethod -Uri "$GatewayBaseUrl$path" -Headers $headers -TimeoutSec 120
}

function Find-RemoteRecord([string]$path, [string]$property, [string]$value) {
    $items = Invoke-ApiGet $path
    return @($items | Where-Object { $_.$property -eq $value } | Select-Object -First 1)[0]
}

function Invoke-CreateSafely(
    [string]$name,
    [string]$path,
    [hashtable]$body,
    [string]$lookupPath,
    [string]$lookupProperty,
    [string]$lookupValue
) {
    $existing = Find-RemoteRecord $lookupPath $lookupProperty $lookupValue
    if ($null -ne $existing) {
        Write-Host "$name EXISTE id=$($existing.id)"
        return $existing
    }

    try {
        $created = Invoke-RestMethod `
            -Uri "$GatewayBaseUrl$path" `
            -Method Post `
            -Headers $headers `
            -Body ($body | ConvertTo-Json -Depth 8) `
            -ContentType "application/json" `
            -TimeoutSec 120
        Write-Host "$name CREADO id=$($created.id)"
        return $created
    } catch {
        # Render puede responder tarde aunque la escritura haya terminado. Se consulta
        # una vez antes de decidir; nunca se reintenta el POST a ciegas.
        try {
            $confirmed = Find-RemoteRecord $lookupPath $lookupProperty $lookupValue
            if ($null -ne $confirmed) {
                Write-Host "$name CONFIRMADO_TRAS_ERROR id=$($confirmed.id)"
                return $confirmed
            }
        } catch {
            Write-Warning "${name}: tampoco se pudo verificar el estado despues del error."
        }

        $script:failures++
        Write-Warning "$name NO_CREADO. Se detuvo este registro sin reintentar ni sobrescribir."
        return $null
    }
}

$healthUrls = @(
    "https://clinica-auth-service.onrender.com/actuator/health",
    "https://clinica-notificaciones-service.onrender.com/actuator/health",
    "https://clinica-citas-service.onrender.com/actuator/health",
    "https://clinica-atencion-medica-service.onrender.com/actuator/health",
    "https://clinica-caja-facturacion-service.onrender.com/actuator/health",
    "$GatewayBaseUrl/actuator/health"
)
foreach ($healthUrl in $healthUrls) {
    try {
        Invoke-RestMethod -Uri $healthUrl -TimeoutSec 45 | Out-Null
        Write-Output "HEALTH_OK $healthUrl"
    } catch {
        Write-Warning "Health sin respuesta inmediata: $healthUrl"
    }
}

$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($AdminPassword)
try {
    $plainAdminPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    $login = Invoke-RestMethod `
        -Uri "$GatewayBaseUrl/api/auth/login" `
        -Method Post `
        -Body (@{ username = "admin"; password = $plainAdminPassword } | ConvertTo-Json) `
        -ContentType "application/json" `
        -TimeoutSec 120
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    $plainAdminPassword = $null
}

if ([string]::IsNullOrWhiteSpace($login.token)) {
    throw "Auth remoto no devolvio un token. No se realizo ninguna escritura."
}
$headers = @{ Authorization = "Bearer $($login.token)" }
$adminProfile = Invoke-ApiGet "/api/auth/me"
if ($adminProfile.username -ne "admin" -or $adminProfile.rol -ne "ADMINISTRADOR") {
    throw "El perfil remoto del administrador no coincide con la cuenta esperada. No se realizo ninguna escritura."
}
Write-Output "CREDENCIAL_OK admin rol=ADMINISTRADOR"

$roles = Invoke-ApiGet "/api/ms/auth/roles"
$roleIds = @{}
foreach ($role in @($roles)) {
    $roleIds[$role.nombre] = $role.id
}

$demoUsers = @(
    @{ Username = "demo_recepcionista"; Name = "Recepcionista Demo Render"; Email = "demo.recepcionista@clinica.local"; Role = "RECEPCIONISTA" },
    @{ Username = "demo_jefe_enfermeria"; Name = "Jefe Enfermeria Demo Render"; Email = "demo.jefe.enfermeria@clinica.local"; Role = "JEFE_ENFERMERIA" },
    @{ Username = "demo_enfermero"; Name = "Enfermero Demo Render"; Email = "demo.enfermero@clinica.local"; Role = "ENFERMERO" },
    @{ Username = "demo_medico"; Name = "Medico Demo Render"; Email = "demo.medico@clinica.local"; Role = "MEDICO" },
    @{ Username = "demo_tecnico_farmacia"; Name = "Tecnico Farmacia Demo Render"; Email = "demo.tecnico.farmacia@clinica.local"; Role = "TECNICO_FARMACIA" },
    @{ Username = "demo_cajero"; Name = "Cajero Demo Render"; Email = "demo.cajero@clinica.local"; Role = "CAJERO" }
)
foreach ($spec in $demoUsers) {
    if (-not $roleIds.ContainsKey($spec.Role)) {
        $script:failures++
        Write-Warning "USUARIO $($spec.Username) omitido: falta rol $($spec.Role)."
        continue
    }
    $username = $spec.Username
    Invoke-CreateSafely "USUARIO $username" "/api/ms/auth/usuarios" @{
        username = $username
        nombreCompleto = $spec.Name
        email = $spec.Email
        password = $DemoUserPassword
        rolId = $roleIds[$spec.Role]
        activo = $true
    } "/api/ms/auth/usuarios" "username" $username | Out-Null
}

$citaSpecs = @(
    @{ Patient = 1; Doctor = 10; Specialty = 2; Date = "2026-09-01"; Start = "08:00"; End = "08:30"; Room = "Consultorio 201" },
    @{ Patient = 1; Doctor = 11; Specialty = 3; Date = "2026-09-01"; Start = "09:00"; End = "09:30"; Room = "Consultorio 102" },
    @{ Patient = 2; Doctor = 11; Specialty = 3; Date = "2026-09-02"; Start = "10:00"; End = "10:30"; Room = "Consultorio 102" },
    @{ Patient = 3; Doctor = 12; Specialty = 4; Date = "2026-09-02"; Start = "11:00"; End = "11:30"; Room = "Consultorio 203" },
    @{ Patient = 4; Doctor = 13; Specialty = 1; Date = "2026-09-03"; Start = "14:30"; End = "15:00"; Room = "Consultorio 101" },
    @{ Patient = 2; Doctor = 10; Specialty = 2; Date = "2026-09-03"; Start = "14:00"; End = "14:30"; Room = "Consultorio 201" },
    @{ Patient = 1; Doctor = 12; Specialty = 4; Date = "2026-09-04"; Start = "16:00"; End = "16:30"; Room = "Consultorio 203" }
)
$citasByIndex = @{}
for ($index = 0; $index -lt $citaSpecs.Count; $index++) {
    $number = $index + 1
    $spec = $citaSpecs[$index]
    $marker = "DEMO ENTREGA - Cita $number"
    $patientPath = "/api/ms/citas/paciente/$($spec.Patient)"
    $citasByIndex[$number] = Invoke-CreateSafely "CITA $number" "/api/ms/citas" @{
        pacienteId = $spec.Patient
        medicoId = $spec.Doctor
        especialidadId = $spec.Specialty
        fecha = $spec.Date
        horaInicio = $spec.Start
        horaFin = $spec.End
        consultorio = $spec.Room
        motivo = $marker
    } $patientPath "motivo" $marker
}

$atencionSpecs = @(
    @{ Patient = 1; History = 1; Doctor = 10; Cita = 1; Diagnosis = "Gastritis probable" },
    @{ Patient = 2; History = 2; Doctor = 11; Cita = 3; Diagnosis = "Control pediatrico estable" },
    @{ Patient = 3; History = 3; Doctor = 12; Cita = 4; Diagnosis = "Lumbalgia mecanica" },
    @{ Patient = 4; History = 4; Doctor = 13; Cita = 5; Diagnosis = "Hipertension controlada" }
)
$atencionesByIndex = @{}
for ($index = 0; $index -lt $atencionSpecs.Count; $index++) {
    $number = $index + 1
    $spec = $atencionSpecs[$index]
    $cita = $citasByIndex[$spec.Cita]
    if ($null -eq $cita) {
        $script:failures++
        Write-Warning "ATENCION $number omitida porque su cita no esta disponible."
        continue
    }
    $marker = "DEMO ENTREGA - Atencion $number"
    $patientPath = "/api/ms/atenciones/paciente/$($spec.Patient)"
    $atencionesByIndex[$number] = Invoke-CreateSafely "ATENCION $number" "/api/ms/atenciones" @{
        historiaClinicaId = $spec.History
        pacienteId = $spec.Patient
        medicoId = $spec.Doctor
        citaId = [string]$cita.id
        motivoConsulta = $marker
        diagnostico = $spec.Diagnosis
        tratamiento = "Tratamiento demo registrado para la entrega"
        indicacionesReceta = "Control medico segun indicacion"
    } $patientPath "motivoConsulta" $marker
}

$debtSpecs = @(
    @{ Patient = 1; Total = 80.00; Payment = 80.00; Method = "EFECTIVO"; Atencion = 1 },
    @{ Patient = 1; Total = 120.00; Payment = 50.00; Method = "TARJETA"; Atencion = 2 },
    @{ Patient = 2; Total = 60.00; Payment = 60.00; Method = "YAPE"; Atencion = 2 },
    @{ Patient = 3; Total = 200.00; Payment = 100.00; Method = "TRANSFERENCIA"; Atencion = 3 },
    @{ Patient = 4; Total = 45.00; Payment = 0.00; Method = "EFECTIVO"; Atencion = 4 }
)
$debtsByIndex = @{}
for ($index = 0; $index -lt $debtSpecs.Count; $index++) {
    $number = $index + 1
    $spec = $debtSpecs[$index]
    $marker = "DEMO ENTREGA - Deuda $number"
    $atencion = $atencionesByIndex[$spec.Atencion]
    $referenceId = if ($null -ne $atencion) { [string]$atencion.id } else { "DEMO-ENTREGA-$number" }
    $debt = Invoke-CreateSafely "DEUDA $number" "/api/ms/caja/deudas" @{
        pacienteId = $spec.Patient
        concepto = $marker
        referenciaTipo = "ATENCION"
        referenciaId = $referenceId
        montoTotal = $spec.Total
    } "/api/ms/caja/deudas" "concepto" $marker
    $debtsByIndex[$number] = $debt

    if ($spec.Payment -le 0 -or $null -eq $debt) {
        continue
    }
    $paymentMarker = "DEMO ENTREGA - Pago $number"
    $paymentPath = "/api/ms/caja/pagos/paciente/$($spec.Patient)"
    Invoke-CreateSafely "PAGO $number" "/api/ms/caja/pagos" @{
        deudaId = $debt.id
        monto = $spec.Payment
        metodoPago = $spec.Method
        observacion = $paymentMarker
    } $paymentPath "observacion" $paymentMarker | Out-Null
}

$notificationSpecs = @(
    @{ Recipient = "PACIENTE"; RecipientId = 1; Type = "CITA"; Channel = "APP"; Reference = "CITA"; ReferenceId = $null },
    @{ Recipient = "PACIENTE"; RecipientId = 1; Type = "PAGO"; Channel = "EMAIL"; Reference = "PAGO"; ReferenceId = $debtsByIndex[1].id },
    @{ Recipient = "PACIENTE"; RecipientId = 2; Type = "CITA"; Channel = "APP"; Reference = "CITA"; ReferenceId = $null },
    @{ Recipient = "MEDICO"; RecipientId = 12; Type = "SISTEMA"; Channel = "APP"; Reference = "ATENCION"; ReferenceId = $atencionesByIndex[3].id },
    @{ Recipient = "PACIENTE"; RecipientId = 3; Type = "PAGO"; Channel = "SMS"; Reference = "DEUDA"; ReferenceId = $debtsByIndex[4].id },
    @{ Recipient = "PACIENTE"; RecipientId = 4; Type = "RECETA"; Channel = "EMAIL"; Reference = "RECETA"; ReferenceId = $null }
)
for ($index = 0; $index -lt $notificationSpecs.Count; $index++) {
    $number = $index + 1
    $spec = $notificationSpecs[$index]
    $marker = "DEMO ENTREGA - Notificacion $number"
    $body = @{
        destinatarioTipo = $spec.Recipient
        destinatarioId = $spec.RecipientId
        tipo = $spec.Type
        canal = $spec.Channel
        titulo = $marker
        mensaje = "Registro de demostracion para la evaluacion final."
        referenciaTipo = $spec.Reference
    }
    if ($null -ne $spec.ReferenceId) {
        $body.referenciaId = $spec.ReferenceId
    }
    Invoke-CreateSafely "NOTIFICACION $number" "/api/ms/notificaciones" $body `
        "/api/ms/notificaciones" "titulo" $marker | Out-Null
}

$finalUsers = Invoke-ApiGet "/api/ms/auth/usuarios"
$finalNotifications = Invoke-ApiGet "/api/ms/notificaciones"
$finalDebts = Invoke-ApiGet "/api/ms/caja/deudas"
$finalCitas = @()
$finalAtenciones = @()
$finalPayments = @()
foreach ($patientId in 1..4) {
    $items = Invoke-ApiGet "/api/ms/citas/paciente/$patientId"
    foreach ($item in @($items)) { $finalCitas += $item }
    $items = Invoke-ApiGet "/api/ms/atenciones/paciente/$patientId"
    foreach ($item in @($items)) { $finalAtenciones += $item }
    $items = Invoke-ApiGet "/api/ms/caja/pagos/paciente/$patientId"
    foreach ($item in @($items)) { $finalPayments += $item }
}

Write-Output "CONTEOS users=$(@($finalUsers).Count) notifications=$(@($finalNotifications).Count) citas=$($finalCitas.Count) atenciones=$($finalAtenciones.Count) debts=$(@($finalDebts).Count) pagos=$($finalPayments.Count)"

foreach ($spec in $demoUsers) {
    try {
        $demoLogin = Invoke-RestMethod `
            -Uri "$GatewayBaseUrl/api/auth/login" `
            -Method Post `
            -Body (@{ username = $spec.Username; password = $DemoUserPassword } | ConvertTo-Json) `
            -ContentType "application/json" `
            -TimeoutSec 120
        if ([string]::IsNullOrWhiteSpace($demoLogin.token) -or $demoLogin.rol -ne $spec.Role) {
            throw "El login no devolvio el token o rol esperado."
        }

        $demoProfile = Invoke-RestMethod `
            -Uri "$GatewayBaseUrl/api/auth/me" `
            -Headers @{ Authorization = "Bearer $($demoLogin.token)" } `
            -TimeoutSec 120
        if ($demoProfile.username -ne $spec.Username -or $demoProfile.rol -ne $spec.Role) {
            throw "El perfil autenticado no coincide con la cuenta esperada."
        }
        Write-Output "CREDENCIAL_OK $($spec.Username) rol=$($spec.Role)"
    } catch {
        $script:failures++
        Write-Warning "CREDENCIAL_ERROR $($spec.Username) rol=$($spec.Role)"
    }
}

if ($script:failures -gt 0) {
    throw "La siembra termino con $script:failures registros omitidos. Revisa las advertencias; no hubo reintentos ciegos."
}
Write-Output "SIEMBRA_RENDER_OK"
