# Clinica Privada - Microservices Container

Repositorio contenedor para los microservicios de la Clinica Privada.

La idea es mantener en una sola carpeta todos los backends independientes que forman la arquitectura de microservicios, parecido a un repositorio contenedor academico.

## Microservicios

| Tipo | Carpeta | Puerto local | Base de datos | Responsabilidad |
| --- | --- | ---: | --- | --- |
| Infraestructura | `Clinica-Config-Server` | 8888 | No aplica | Centraliza configuracion de microservicios |
| Infraestructura | `Clinica-Eureka-Server` | 8761 | No aplica | Registro y descubrimiento de servicios |
| Infraestructura | `Clinica-Auth-Service` | 8091 | MySQL | Login, usuarios, roles y emision de JWT |
| Apoyo | `Clinica-Notificaciones-Service` | 8092 | PostgreSQL | Registro y consulta de notificaciones |
| Negocio | `Clinica-Citas-Service` | 8093 | MongoDB | Reserva, disponibilidad y cancelacion de citas |
| Infraestructura | `Clinica-Gateway-Service` | 8090 | No aplica | Entrada unica y enrutamiento hacia los microservicios |

La propuesta completa con tres microservicios de negocio esta documentada en `PROPUESTA_MICROSERVICIOS.md`.

## Estructura

```text
Clinica-Privada-Microservices-Container/
  Clinica-Config-Server/
  Clinica-Eureka-Server/
  Clinica-Auth-Service/
  Clinica-Notificaciones-Service/
  Clinica-Citas-Service/
  Clinica-Gateway-Service/
  scripts/
  pom.xml
  docker-compose.microservices.yml
  GUIA_MICROSERVICIOS_PROFESORA.md
  PROPUESTA_MICROSERVICIOS.md
```

## Orden de arranque

El flujo sigue el orden trabajado en clase:

```text
Bases de datos
   -> Config Server
   -> Eureka Server
   -> Microservicios de negocio/apoyo/infraestructura
   -> Gateway
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

URLs principales:

```text
Config Server: http://localhost:8888/clinica-gateway-service/default
Eureka:        http://localhost:8761
Gateway:       http://localhost:8090
```

## Variables de entorno

Cada microservicio tiene su propio archivo `.env.example`.

Los archivos `.env` reales no se suben a GitHub porque contienen contrasenas y configuracion local.

## Notas

- Cada microservicio tiene su propio `Dockerfile`.
- Cada microservicio tiene su propio `pom.xml`.
- La raiz tiene un `pom.xml` padre con modulos, igual que un repositorio contenedor Maven.
- Cada microservicio usa su propia base de datos.
- Config Server y Eureka Server no tienen base de datos porque son servicios de infraestructura.
- El Gateway no guarda datos; solo enruta solicitudes.
- Kubernetes se puede agregar despues en una carpeta `k8s/`.
