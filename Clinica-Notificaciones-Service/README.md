# Clinica Notificaciones Service

Microservicio de apoyo para registrar y consultar notificaciones del sistema clinico.

## Responsabilidad

Este servicio no agenda citas, no cobra pagos y no autentica usuarios. Solo registra mensajes derivados de otros procesos, por ejemplo:

- Confirmacion de cita.
- Pago registrado.
- Receta lista.
- Avisos internos del sistema.

## Base de datos propia

Usa PostgreSQL como gestor de base de datos.

Base de datos:

```text
clinica_notificaciones_db
```

Tabla principal:

- `notificaciones`

## Seguridad

Valida tokens JWT emitidos por `Clinica-Auth-Service`. No consulta la base de datos de Auth; solo verifica la firma y expiracion del token.

## Ejecucion local recomendada

Desde la raiz del contenedor ejecuta:

```powershell
.\scripts\start-microservices.ps1
```

Así se carga el `.env` raiz, se inicia PostgreSQL, se ejecutan las semillas y luego arranca Notificaciones en:

```text
http://localhost:8092
```

## Endpoints principales

```text
POST  /api/notificaciones
GET   /api/notificaciones
GET   /api/notificaciones/destinatario/{destinatarioTipo}/{destinatarioId}
GET   /api/notificaciones/resumen
PATCH /api/notificaciones/{id}/leida
GET   /actuator/health
```

## Prueba via Gateway

Primero obten un token desde Auth por Gateway:

```powershell
$body = @{ username = "admin"; password = "ClinicaAdminLocal123!" } | ConvertTo-Json
$login = Invoke-RestMethod -Uri "http://localhost:8090/api/auth/login" -Method Post -Body $body -ContentType "application/json"
```

Luego crea una notificacion:

```powershell
$notif = @{
  destinatarioTipo = "PACIENTE"
  destinatarioId = 1
  tipo = "CITA"
  canal = "APP"
  titulo = "Cita confirmada"
  mensaje = "Tu cita fue registrada correctamente."
  referenciaTipo = "CITA"
  referenciaId = 1001
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8090/api/ms/notificaciones" -Method Post -Body $notif -ContentType "application/json" -Headers @{ Authorization = "Bearer $($login.token)" }
```

## Clasificacion

Este microservicio es de apoyo porque acompana procesos principales, pero no es el nucleo clinico ni infraestructura tecnica base.
