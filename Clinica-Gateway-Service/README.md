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
/api/ms/citas/**         -> Citas Service
/api/ms/atenciones/**    -> Atencion Medica Service
/api/ms/caja/**          -> Caja Facturacion Service
/api/ms/notificaciones/** -> Notificaciones Service
/api/**                  -> Backend monolitico como fallback
```

Las tres banderas de rutas heredadas se mantienen en `false`: así el frontend conserva sus contratos con el backend principal y `/api/ms/**` permite probar los servicios nuevos sin ambiguedad.

## Ejecutar

La ruta soportada carga todas las variables y dependencias desde la raiz:

```powershell
.\scripts\start-microservices.ps1
```

El Gateway arranca despues de que Config Server, Eureka y los microservicios estan saludables.

## Probar

```powershell
Invoke-RestMethod -Uri "http://localhost:8090/actuator/health"
Invoke-RestMethod -Uri "http://localhost:8090/gateway/routes"
```

Si Auth Service esta levantado, este login pasa por el Gateway:

```powershell
$body = @{ username = "admin"; password = "ClinicaAdminLocal123!" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8090/api/auth/login" -Method Post -Body $body -ContentType "application/json"
```

## Clasificacion

Este componente es infraestructura porque organiza la comunicacion entre frontend, backend monolitico y microservicios.
