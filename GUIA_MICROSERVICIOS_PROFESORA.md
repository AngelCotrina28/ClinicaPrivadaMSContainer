# Guia de microservicios implementados

Este proyecto ahora conserva el backend original, pero agrega una arquitectura de microservicios separada para cumplir el requerimiento academico.

## Resumen

Se implementaron 3 microservicios con base de datos propia y gestor distinto:

| Tipo pedido | Microservicio | Puerto local | Gestor | Base de datos propia | Responsabilidad |
| --- | --- | ---: | --- | --- | --- |
| Infraestructura | `Clinica-Auth-Service` | 8091 | MySQL | `clinica_auth_db` | Login, usuarios, roles y JWT |
| Apoyo | `Clinica-Notificaciones-Service` | 8092 | PostgreSQL | `clinica_notificaciones_db` | Avisos y notificaciones del sistema |
| Negocio | `Clinica-Citas-Service` | 8093 | SQL Server | `clinica_citas_db` | Reserva, disponibilidad y cancelacion de citas |

Ademas, se implemento un API Gateway:

| Componente | Puerto local | Base de datos | Responsabilidad |
| --- | ---: | --- | --- |
| `Clinica-Gateway-Service` | 8090 | No aplica | Entrada unica y enrutamiento hacia los servicios |

El Gateway no guarda informacion de negocio. Por eso no necesita base de datos y no se cuenta como uno de los 3 microservicios con datos propios.

## Por que cada microservicio tiene su propia BD y gestor

En microservicios, cada servicio debe ser dueño de sus datos. Eso evita que otro servicio toque sus tablas directamente.

En esta implementacion, ademas, cada microservicio usa un gestor diferente para cumplir el requisito de la profesora:

- Auth usa MySQL.
- Notificaciones usa PostgreSQL.
- Citas usa SQL Server.

Ejemplo:

- Auth guarda usuarios y roles en `clinica_auth_db` con MySQL.
- Citas no consulta directamente la tabla `usuarios`.
- Citas solo valida el JWT emitido por Auth.
- Notificaciones guarda sus mensajes en `clinica_notificaciones_db` con PostgreSQL.
- Citas guarda sus reservas en `clinica_citas_db` con SQL Server.

## Arquitectura local

```text
Frontend Angular
   |
   v
Clinica-Gateway-Service :8090
   |
   |-- /api/auth/**             -> Clinica-Auth-Service :8091
   |                                Gestor: MySQL
   |                                BD: clinica_auth_db
   |
   |-- /api/notificaciones/**   -> Clinica-Notificaciones-Service :8092
   |                                Gestor: PostgreSQL
   |                                BD: clinica_notificaciones_db
   |
   |-- /api/citas/**            -> Clinica-Citas-Service :8093
   |                                Gestor: SQL Server
   |                                BD: clinica_citas_db
   |
   |-- /api/** restante         -> Backend original :8080
```

## Bases de datos creadas

| Gestor | Base de datos | Usuario |
| --- | --- | --- |
| MySQL | `clinica_auth_db` | `clinica_auth_user` |
| PostgreSQL | `clinica_notificaciones_db` | `clinica_notificaciones_user` |
| SQL Server | `clinica_citas_db` | `sa` local / usuario SQL Server en despliegue |

## Endpoints principales

### Auth Service

```text
POST /api/auth/login
GET  /api/auth/me
GET  /api/auth/roles
GET  /api/auth/usuarios
POST /api/auth/usuarios
```

### Notificaciones Service

```text
POST  /api/notificaciones
GET   /api/notificaciones
GET   /api/notificaciones/destinatario/{destinatarioTipo}/{destinatarioId}
GET   /api/notificaciones/resumen
PATCH /api/notificaciones/{id}/leida
```

### Citas Service

```text
POST  /api/citas
GET   /api/citas/{id}
GET   /api/citas/paciente/{pacienteId}
GET   /api/citas/medico/{medicoId}?fecha=2026-07-05
GET   /api/citas/disponibilidad?medicoId=10&fecha=2026-07-05
PATCH /api/citas/{id}/cancelar
```

## Frase para exposicion

El sistema original era un monolito modular. Para cumplir el requerimiento se agregaron tres microservicios independientes: Auth como infraestructura, Notificaciones como apoyo y Citas como negocio. Cada uno corre como una aplicacion separada, expone su propia API y usa una base de datos propia con un gestor diferente: MySQL, PostgreSQL y SQL Server. El Gateway se agrego como componente de entrada para enrutar las peticiones, pero no guarda datos.
