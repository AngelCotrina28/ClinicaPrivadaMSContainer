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
| Negocio | `Clinica-Atencion-Medica-Service` | 8094 | PostgreSQL | Atenciones, diagnosticos, historial y recetas |
| Negocio | `Clinica-Caja-Facturacion-Service` | 8095 | MySQL | Deudas, pagos, comprobantes y caja |
| Infraestructura | `Clinica-Gateway-Service` | 8090 | No aplica | Entrada unica y enrutamiento hacia los microservicios |

La arquitectura completa con tres microservicios de negocio esta documentada en `PROPUESTA_MICROSERVICIOS.md`.

## Estructura

```text
Clinica-Privada-Microservices-Container/
  Clinica-Config-Server/
  Clinica-Eureka-Server/
  Clinica-Auth-Service/
  Clinica-Citas-Service/
  Clinica-Atencion-Medica-Service/
  Clinica-Caja-Facturacion-Service/
  Clinica-Notificaciones-Service/
  Clinica-Gateway-Service/
  database/
  k8s/
  postman/
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

## Ejecucion local con Docker Compose

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

El Gateway publica dos tipos de rutas:

```text
/api/ms/citas/**       -> Citas Service
/api/ms/atenciones/**  -> Atencion Medica Service
/api/ms/caja/**        -> Caja Facturacion Service
/api/ms/notificaciones/** -> Notificaciones Service
/api/**                -> rutas principales y compatibilidad con el backend original
```

El prefijo `/api/ms` permite probar directamente los microservicios nuevos sin
romper las rutas heredadas que el frontend todavia utiliza durante la migracion.

Para apagar Docker Compose:

```powershell
.\scripts\stop-microservices.ps1
```

## Ejecucion local con Kubernetes

La carpeta `k8s/` contiene los manifiestos equivalentes al Compose, siguiendo el orden visto en clase:

```text
Namespace y configuracion
   -> Bases de datos
   -> Config Server
   -> Eureka Server
   -> Microservicios
   -> Gateway
```

Antes de ejecutarlo, habilita Kubernetes en Docker Desktop:

```text
Docker Desktop -> Settings -> Kubernetes -> Enable Kubernetes
```

Luego ejecuta:

```powershell
.\scripts\start-k8s.ps1
.\scripts\status-k8s.ps1
.\scripts\verify-k8s.ps1
```

Para apagar Kubernetes:

```powershell
.\scripts\stop-k8s.ps1
```

## Variables de entorno

Cada microservicio tiene su propio archivo `.env.example`.

Los archivos `.env` reales no se suben a GitHub porque contienen contrasenas y configuracion local.

## Esquemas y pruebas API

- `database/` contiene los esquemas y datos de ejemplo para MySQL, PostgreSQL y MongoDB.
- `postman/` contiene la coleccion completa y ambientes para ejecucion local y Render.
- `docs/` contiene el informe editable, el PDF y los diagramas arquitectonicos.
- `scripts/verify-microservices.ps1` ejecuta el mismo flujo principal desde PowerShell.

## Notas

- Cada microservicio tiene su propio `Dockerfile`.
- Cada microservicio tiene su propio `pom.xml`.
- La raiz tiene un `pom.xml` padre con modulos, igual que un repositorio contenedor Maven.
- Cada microservicio usa su propia base de datos.
- Config Server y Eureka Server no tienen base de datos porque son servicios de infraestructura.
- El Gateway no guarda datos; solo enruta solicitudes.
- Kubernetes esta documentado en la carpeta `k8s/`.
