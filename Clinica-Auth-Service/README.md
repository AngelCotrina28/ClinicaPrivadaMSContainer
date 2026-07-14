# Clinica Auth Service

Microservicio de infraestructura para autenticacion, usuarios, roles y emision de JWT.

## Responsabilidad

Este servicio no gestiona citas, pagos ni atencion medica. Su unica responsabilidad es autenticar usuarios y entregar informacion de seguridad para el resto del sistema.

## Base de datos propia

Usa MySQL como gestor de base de datos.

Base de datos:

```text
clinica_auth_db
```

Tablas principales:

- `roles`
- `usuarios`

## Ejecucion local

```powershell
cd Clinica-Auth-Service
.\mvnw.cmd -DskipTests package
java -jar target\auth-service-0.0.1-SNAPSHOT.jar
```

Por defecto local corre en:

```text
http://localhost:8091
```

## Usuario inicial

Si la BD esta vacia y `AUTH_SEED_ENABLED=true`, crea:

```text
username: admin
password: valor configurado en SYSTEM_ADMIN_PASSWORD
rol: ADMINISTRADOR
```

## Endpoints principales

```text
POST /api/auth/login
GET  /api/auth/me
GET  /api/auth/roles
GET  /api/auth/usuarios
POST /api/auth/usuarios
GET  /actuator/health
```

## Prueba rapida

```powershell
$body = @{ username = "admin"; password = $env:SYSTEM_ADMIN_PASSWORD } | ConvertTo-Json
$login = Invoke-RestMethod -Uri "http://localhost:8091/api/auth/login" -Method Post -Body $body -ContentType "application/json"
$login.token
```

Luego usa el token:

```powershell
Invoke-RestMethod -Uri "http://localhost:8091/api/auth/roles" -Headers @{ Authorization = "Bearer $($login.token)" }
```

## Clasificacion

Este microservicio es de infraestructura porque sostiene la seguridad transversal del sistema: login, roles, usuarios y tokens.
