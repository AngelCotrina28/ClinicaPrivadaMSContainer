# Clinica Citas Service

Microservicio de negocio para gestion de citas medicas.

## Responsabilidad

Este servicio representa una capacidad central de la clinica:

- Crear citas.
- Consultar citas por paciente o medico.
- Consultar disponibilidad del medico.
- Cancelar citas.
- Evitar cruces de horario para el mismo medico.

## Base de datos propia

Usa MongoDB como gestor de base de datos.

Base de datos:

```text
clinica_citas_db
```

Coleccion principal:

- `citas`

## Seguridad

Valida tokens JWT emitidos por `Clinica-Auth-Service`. No consulta la base de datos de Auth.

## Ejecucion local

```powershell
cd Clinica-Citas-Service
.\mvnw.cmd -DskipTests package
java -jar target\citas-service-0.0.1-SNAPSHOT.jar
```

Por defecto corre en:

```text
http://localhost:8093
```

## Endpoints principales

```text
POST  /api/citas
GET   /api/citas/{id}
GET   /api/citas/paciente/{pacienteId}
GET   /api/citas/medico/{medicoId}?fecha=2026-07-04
GET   /api/citas/disponibilidad?medicoId=1&fecha=2026-07-04
PATCH /api/citas/{id}/cancelar
GET   /actuator/health
```

## Prueba via Gateway

Activa `CITAS_ROUTE_ENABLED=true` en `Clinica-Gateway-Service/.env` y reinicia el Gateway.

```powershell
$body = @{ username = "admin"; password = $env:AUTH_ADMIN_PASSWORD } | ConvertTo-Json
$login = Invoke-RestMethod -Uri "http://localhost:8090/api/auth/login" -Method Post -Body $body -ContentType "application/json"

$cita = @{
  pacienteId = 1
  medicoId = 10
  especialidadId = 2
  fecha = "2026-07-05"
  horaInicio = "09:00"
  horaFin = "09:30"
  consultorio = "Consultorio 201"
  motivo = "Consulta general"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8090/api/citas" -Method Post -Body $cita -ContentType "application/json" -Headers @{ Authorization = "Bearer $($login.token)" }
```

## Clasificacion

Este microservicio es de negocio porque gestiona una operacion principal de la clinica: la reserva y administracion de citas medicas.
