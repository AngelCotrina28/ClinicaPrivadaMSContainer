# Guia de microservicios implementados

Este proyecto ahora conserva el backend original, pero agrega una arquitectura de microservicios separada para cumplir el requerimiento academico.

## Resumen

Se implementaron 3 microservicios con base de datos propia y gestor distinto:

| Tipo pedido | Microservicio | Puerto local | Gestor | Base de datos propia | Responsabilidad |
| --- | --- | ---: | --- | --- | --- |
| Infraestructura | `Clinica-Auth-Service` | 8091 | MySQL | `clinica_auth_db` | Login, usuarios, roles y JWT |
| Apoyo | `Clinica-Notificaciones-Service` | 8092 | PostgreSQL | `clinica_notificaciones_db` | Avisos y notificaciones del sistema |
| Negocio | `Clinica-Citas-Service` | 8093 | MongoDB | `clinica_citas_db` | Reserva, disponibilidad y cancelacion de citas |

Ademas, se implementaron servicios de infraestructura para seguir la arquitectura vista en clase:

| Componente | Puerto local | Base de datos | Responsabilidad |
| --- | ---: | --- | --- |
| `Clinica-Config-Server` | 8888 | No aplica | Centraliza configuraciones |
| `Clinica-Eureka-Server` | 8761 | No aplica | Registro y descubrimiento de servicios |
| `Clinica-Gateway-Service` | 8090 | No aplica | Entrada unica y enrutamiento hacia los servicios |

Config, Eureka y Gateway no guardan informacion de negocio. Por eso no necesitan base de datos y no se cuentan como microservicios con datos propios.

## Por que cada microservicio tiene su propia BD y gestor

En microservicios, cada servicio debe ser dueño de sus datos. Eso evita que otro servicio toque sus tablas directamente.

En esta implementacion, ademas, cada microservicio usa un gestor diferente para cumplir el requisito de la profesora:

- Auth usa MySQL.
- Notificaciones usa PostgreSQL.
- Citas usa MongoDB.

Ejemplo:

- Auth guarda usuarios y roles en `clinica_auth_db` con MySQL.
- Citas no consulta directamente la tabla `usuarios`.
- Citas solo valida el JWT emitido por Auth.
- Notificaciones guarda sus mensajes en `clinica_notificaciones_db` con PostgreSQL.
- Citas guarda sus reservas en `clinica_citas_db` con MongoDB.

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
   |                                Gestor: MongoDB
   |                                BD: clinica_citas_db
   |
   |-- /api/** restante         -> Backend original :8080

Servicios de infraestructura:

Clinica-Config-Server :8888
   |-- entrega configuracion centralizada

Clinica-Eureka-Server :8761
   |-- registra Auth, Citas, Notificaciones y Gateway
```

## Bases de datos creadas

| Gestor | Base de datos | Usuario |
| --- | --- | --- |
| MySQL | `clinica_auth_db` | `clinica_auth_user` |
| PostgreSQL | `clinica_notificaciones_db` | `clinica_notificaciones_user` |
| MongoDB | `clinica_citas_db` | `clinica_citas_admin` local / usuario MongoDB en despliegue |

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

El sistema original era un monolito modular. Para cumplir el requerimiento se agregaron microservicios independientes: Auth como infraestructura, Notificaciones como apoyo y Citas como negocio implementado. Cada uno corre como una aplicacion separada, expone su propia API y usa una base de datos propia cuando corresponde. La arquitectura usa tres gestores: MySQL, PostgreSQL y MongoDB. Tambien se agregaron Config Server, Eureka Server y Gateway para seguir el patron de clase: configuracion centralizada, descubrimiento de servicios y entrada unica al sistema.
