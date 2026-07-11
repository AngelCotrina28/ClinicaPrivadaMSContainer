# Despliegue local con Kubernetes

Estos manifiestos replican la arquitectura del `docker-compose.microservices.yml`:

1. Bases de datos.
2. Config Server.
3. Eureka Server.
4. Microservicios de negocio/apoyo/infraestructura.
5. Gateway.

## Requisitos

- Docker Desktop con Kubernetes habilitado.
- Imagenes locales ya construidas por Docker Compose o Maven/Docker.
- Contexto de Kubernetes apuntando a Docker Desktop.

```powershell
kubectl config use-context docker-desktop
```

Si `kubectl config get-contexts` no muestra `docker-desktop`, falta habilitar Kubernetes en Docker Desktop:

```text
Docker Desktop -> Settings -> Kubernetes -> Enable Kubernetes
```

## Desplegar con script

```powershell
.\scripts\start-k8s.ps1
```

El script valida el contexto `docker-desktop`, reutiliza o construye las imagenes locales `clinica-*:local`, aplica los manifiestos de `k8s/` y espera los deployments.

El orden de despliegue replica la clase:

```text
Namespace/configuracion
  -> Bases de datos
  -> Config Server y Eureka Server
  -> Microservicios
  -> Gateway
```

## Ver estado sin probar flujo funcional

```powershell
.\scripts\status-k8s.ps1
```

## Desplegar manualmente

```powershell
kubectl apply -f k8s/
kubectl -n clinica-ms get pods -w
```

## Exponer servicios para probar desde el navegador/Postman

Ejecuta cada comando en una terminal distinta:

```powershell
kubectl -n clinica-ms port-forward svc/config-server 8888:8888
kubectl -n clinica-ms port-forward svc/eureka-server 8761:8761
kubectl -n clinica-ms port-forward svc/gateway-service 8090:8090
```

URLs:

```text
Config Server: http://localhost:8888/clinica-gateway-service/default
Eureka:        http://localhost:8761
Gateway:       http://localhost:8090
```

## Probar

Con script:

```powershell
.\scripts\verify-k8s.ps1
```

Manual:

```powershell
Invoke-RestMethod http://localhost:8090/actuator/health
Invoke-RestMethod http://localhost:8090/gateway/routes
```

Login:

```powershell
$body = @{ username = "admin"; password = "ClinicaAdminLocal123!" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8090/api/auth/login" -Method Post -Body $body -ContentType "application/json"
```

## Eliminar

Con script:

```powershell
.\scripts\stop-k8s.ps1
```

Manual:

```powershell
kubectl delete namespace clinica-ms
```
