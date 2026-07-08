# Propuesta de microservicios

La propuesta implementada para el sistema de Clinica Privada separa el sistema en microservicios segun responsabilidades reales del negocio clinico, usando tres motores de base de datos.

## Microservicios de negocio

| Microservicio | Que hace | Motor de BD |
| --- | --- | --- |
| `Clinica-Citas-Service` | Gestiona reserva, disponibilidad, consulta y cancelacion de citas medicas. | MongoDB |
| `Clinica-Atencion-Medica-Service` | Gestiona atenciones medicas, diagnosticos, historial clinico, tratamientos y recetas. | PostgreSQL |
| `Clinica-Caja-Facturacion-Service` | Gestiona deudas, pagos, comprobantes y resumen de caja. | MySQL |

## Microservicios de infraestructura

| Microservicio | Que hace | Motor de BD |
| --- | --- | --- |
| `Clinica-Config-Server` | Centraliza la configuracion de los demas microservicios por ambiente. | No requiere BD |
| `Clinica-Eureka-Server` | Registra los microservicios activos y permite descubrirlos sin IPs fijas. | No requiere BD |
| `Clinica-Gateway-Service` | Recibe las peticiones del frontend y las redirige al microservicio correspondiente. | No requiere BD |
| `Clinica-Auth-Service` | Gestiona login, usuarios, roles y emision de tokens JWT. | MySQL |

## Microservicio de apoyo

| Microservicio | Que hace | Motor de BD |
| --- | --- | --- |
| `Clinica-Notificaciones-Service` | Registra y consulta avisos del sistema, como confirmacion de citas o pagos pendientes. | PostgreSQL |

## Motores de base de datos

| Motor | Microservicios |
| --- | --- |
| MySQL | `Clinica-Auth-Service`, `Clinica-Caja-Facturacion-Service` |
| PostgreSQL | `Clinica-Atencion-Medica-Service`, `Clinica-Notificaciones-Service` |
| MongoDB | `Clinica-Citas-Service` |

Aunque algunos microservicios comparten el mismo motor, cada microservicio mantiene su propia base de datos o esquema. De esta forma se respeta la independencia de datos de cada microservicio.

## Exposicion breve

Nuestra arquitectura de microservicios para la clinica privada separa el sistema en tres microservicios principales de negocio: Citas, Atencion Medica y Caja/Facturacion.

`Clinica-Citas-Service` gestiona la agenda de citas medicas, disponibilidad de medicos y cancelaciones. Usara MongoDB porque una cita puede almacenarse como un documento completo con paciente, medico, fecha, horario, estado y consultorio.

`Clinica-Atencion-Medica-Service` gestiona el proceso clinico del paciente: atenciones, diagnosticos, historial, tratamiento e indicaciones de receta. Usa PostgreSQL porque requiere informacion clinica estructurada y consistente.

`Clinica-Caja-Facturacion-Service` gestiona la parte economica: deudas, pagos, comprobantes y resumen de caja. Usa MySQL porque es adecuado para procesos transaccionales.

Ademas, se proponen microservicios de infraestructura como Auth, Gateway, Config Server y Eureka Server, y un microservicio de apoyo para Notificaciones.
