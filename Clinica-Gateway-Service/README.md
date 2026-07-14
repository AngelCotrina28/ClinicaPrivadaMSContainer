# Clinica Gateway Service

API Gateway para exponer una entrada unica hacia el sistema de clinica.

## Responsabilidad

Este servicio no guarda datos ni reemplaza a los microservicios. Su trabajo es recibir peticiones HTTP y enviarlas al servicio correcto.

No se cuenta como uno de los microservicios de negocio con base de datos propia. El Gateway es infraestructura adicional para ordenar la comunicacion entre frontend, backend monolitico y microservicios.

Los 3 microservicios de negocio implementados son:

- Citas
- Atencion Medica
- Caja y Facturacion

Auth tambien es infraestructura y Notificaciones es apoyo.

## Rutas locales

```text
Gateway:              http://localhost:8090
Auth Service:         http://localhost:8091
Notificaciones:       http://localhost:8092
Citas Service:        http://localhost:8093
Atencion Medica:      http://localhost:8094
Caja Facturacion:     http://localhost:8095
Backend monolitico:   http://localhost:8080
```

## Reglas de enrutamiento

```text
/api/auth/**             -> Auth Service
/api/notificaciones/**   -> Notificaciones Service
/api/citas/**            -> Citas Service cuando CITAS_LEGACY_ROUTE_ENABLED=true
/api/atenciones/**       -> Atencion Medica Service cuando ATENCION_LEGACY_ROUTE_ENABLED=true
/api/caja/**             -> Caja Facturacion Service cuando CAJA_LEGACY_ROUTE_ENABLED=true
/api/**                  -> Backend monolitico como fallback
```

Las rutas de negocio se activan con flags para permitir migracion gradual. Si un flag esta en `false`, el Gateway deja que esa ruta siga al backend monolitico como fallback.

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
