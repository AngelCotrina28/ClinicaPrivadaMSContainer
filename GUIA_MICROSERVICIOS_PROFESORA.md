# Guia de microservicios implementados

Este proyecto ahora conserva el backend original, pero agrega una arquitectura de microservicios separada para cumplir el requerimiento academico.

## Resumen

Se implementaron 3 microservicios de negocio y servicios de infraestructura/apoyo:

| Tipo pedido | Microservicio | Puerto local | Gestor | Base de datos propia | Responsabilidad |
| --- | --- | ---: | --- | --- | --- |
| Infraestructura | `Clinica-Auth-Service` | 8091 | MySQL | `clinica_auth_db` | Login, usuarios, roles y JWT |
| Apoyo | `Clinica-Notificaciones-Service` | 8092 | PostgreSQL | `clinica_notificaciones_db` | Avisos y notificaciones del sistema |
| Negocio | `Clinica-Citas-Service` | 8093 | MongoDB | `clinica_citas_db` | Reserva, disponibilidad y cancelacion de citas |
| Negocio | `Clinica-Atencion-Medica-Service` | 8094 | PostgreSQL | `clinica_atencion_db` | Atenciones, diagnosticos, historial y recetas |
| Negocio | `Clinica-Caja-Facturacion-Service` | 8095 | MySQL | `clinica_caja_db` | Deudas, pagos, comprobantes y caja |

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

- Auth y Caja usan MySQL, cada uno con su propia base de datos.
- Notificaciones y Atencion Medica usan PostgreSQL, cada uno con su propia base de datos.
- Citas usa MongoDB.

Ejemplo:

- Auth guarda usuarios y roles en `clinica_auth_db` con MySQL.
- Los microservicios de negocio no consultan directamente la tabla `usuarios`.
- Los microservicios de negocio validan el JWT emitido por Auth.
- Notificaciones guarda sus mensajes en `clinica_notificaciones_db` con PostgreSQL.
- Citas guarda sus reservas en `clinica_citas_db` con MongoDB.
- Atencion Medica guarda sus registros clinicos en `clinica_atencion_db` con PostgreSQL.
- Caja guarda sus deudas y pagos en `clinica_caja_db` con MySQL.

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
   |-- /api/atenciones/**       -> Clinica-Atencion-Medica-Service :8094
   |                                Gestor: PostgreSQL
   |                                BD: clinica_atencion_db
   |
   |-- /api/caja/**             -> Clinica-Caja-Facturacion-Service :8095
   |                                Gestor: MySQL
   |                                BD: clinica_caja_db
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
| MySQL | `clinica_caja_db` | `clinica_caja_user` |
| PostgreSQL | `clinica_atencion_db` | `clinica_atencion_user` |
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

### Atencion Medica Service

```text
POST  /api/atenciones
GET   /api/atenciones/{id}
GET   /api/atenciones/historia/{historiaClinicaId}
GET   /api/atenciones/paciente/{pacienteId}
GET   /api/atenciones/medico/{medicoId}
PATCH /api/atenciones/{id}/cerrar
PATCH /api/atenciones/{id}/anular
```

### Caja Facturacion Service

```text
POST /api/caja/deudas
GET  /api/caja/deudas
GET  /api/caja/deudas/{id}
POST /api/caja/pagos
GET  /api/caja/pagos/{id}
GET  /api/caja/pagos/paciente/{pacienteId}
GET  /api/caja/resumen
```

## Frase para exposicion

El sistema original era un monolito modular. Para cumplir el requerimiento se agregaron tres microservicios de negocio: Citas, Atencion Medica y Caja/Facturacion. Cada uno corre como una aplicacion separada, expone su propia API y usa una base de datos propia. La arquitectura usa tres gestores: MySQL, PostgreSQL y MongoDB. Tambien se agregaron Auth como infraestructura, Notificaciones como apoyo, y Config Server, Eureka Server y Gateway para seguir el patron de clase: configuracion centralizada, descubrimiento de servicios y entrada unica al sistema.
