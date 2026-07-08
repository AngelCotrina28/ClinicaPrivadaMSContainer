# Clinica Privada - Microservices Container

Repositorio contenedor para los microservicios de la Clinica Privada.

La idea es mantener en una sola carpeta todos los backends independientes que forman la arquitectura de microservicios, parecido a un repositorio contenedor academico.

## Microservicios

| Tipo | Carpeta | Puerto local | Base de datos | Responsabilidad |
| --- | --- | ---: | --- | --- |
| Infraestructura | `Clinica-Auth-Service` | 8091 | MySQL | Login, usuarios, roles y emision de JWT |
| Apoyo | `Clinica-Notificaciones-Service` | 8092 | PostgreSQL | Registro y consulta de notificaciones |
| Negocio | `Clinica-Citas-Service` | 8093 | SQL Server | Reserva, disponibilidad y cancelacion de citas |
| Gateway | `Clinica-Gateway-Service` | 8090 | No aplica | Entrada unica y enrutamiento hacia los microservicios |

## Estructura

```text
Clinica-Privada-Microservices-Container/
  Clinica-Auth-Service/
  Clinica-Notificaciones-Service/
  Clinica-Citas-Service/
  Clinica-Gateway-Service/
  scripts/
  docker-compose.microservices.yml
  GUIA_MICROSERVICIOS_PROFESORA.md
```

## Ejecucion local

Requisitos:

- Java 21
- Docker Desktop
- PowerShell

Comandos:

```powershell
.\scripts\start-microservices.ps1
.\scripts\verify-microservices.ps1
```

## Variables de entorno

Cada microservicio tiene su propio archivo `.env.example`.

Los archivos `.env` reales no se suben a GitHub porque contienen contrasenas y configuracion local.

## Notas

- Cada microservicio tiene su propio `Dockerfile`.
- Cada microservicio tiene su propio `pom.xml`.
- Cada microservicio usa su propia base de datos.
- El Gateway no guarda datos; solo enruta solicitudes.
- Kubernetes se puede agregar despues en una carpeta `k8s/`.
