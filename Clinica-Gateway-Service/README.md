# Clinica Gateway Service

API Gateway para exponer una entrada unica hacia el sistema de clinica.

## Responsabilidad

Este servicio no guarda datos ni reemplaza a los microservicios. Su trabajo es recibir peticiones HTTP y enviarlas al servicio correcto.

No se cuenta como uno de los 3 microservicios con base de datos propia. Los 3 microservicios pedidos son Auth, Notificaciones y Citas. El Gateway es infraestructura adicional para ordenar la comunicacion.

## Rutas locales

```text
Gateway:              http://localhost:8090
Auth Service:         http://localhost:8091
Notificaciones:       http://localhost:8092
Citas Service:        http://localhost:8093
Backend monolitico:   http://localhost:8080
```

## Reglas de enrutamiento

```text
/api/auth/**             -> Auth Service
/api/notificaciones/**   -> Notificaciones Service
/api/citas/**            -> Citas Service cuando CITAS_ROUTE_ENABLED=true
/api/**                  -> Backend monolitico como fallback
```

Mientras el microservicio de citas no exista, `CITAS_ROUTE_ENABLED=false` mantiene `/api/citas/**` apuntando al backend actual.

## Ejecutar

```powershell
cd Clinica-Gateway-Service
.\mvnw.cmd -DskipTests package
java -jar target\gateway-service-0.0.1-SNAPSHOT.jar
```

## Probar

```powershell
Invoke-RestMethod -Uri "http://localhost:8090/actuator/health"
Invoke-RestMethod -Uri "http://localhost:8090/gateway/routes"
```

Si Auth Service esta levantado, este login pasa por el Gateway:

```powershell
$body = @{ username = "admin"; password = $env:AUTH_ADMIN_PASSWORD } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8090/api/auth/login" -Method Post -Body $body -ContentType "application/json"
```

## Clasificacion

Este componente es infraestructura porque organiza la comunicacion entre frontend, backend monolitico y microservicios.
