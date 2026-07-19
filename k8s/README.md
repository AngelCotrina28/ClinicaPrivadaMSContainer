# Kubernetes local

Estos manifiestos reproducen la arquitectura de Docker Compose en el namespace `clinica-ms`.

## Requisitos

- Docker Desktop con Kubernetes habilitado.
- Contexto `docker-desktop` disponible.
- PowerShell y `kubectl` en PATH.
- `.env` valido en la raiz; si falta, el script lo crea desde `.env.example`.

Si la terminal bloquea `.ps1`, ejecuta primero `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass`.

## Desplegar

Desde la raiz del repositorio:

```powershell
.\scripts\start-k8s.ps1
```

El script es la entrada recomendada porque hay recursos que no deben contener secretos versionados. Realiza este orden:

```text
Namespace
  -> ConfigMap y Secret generados desde .env
  -> cinco bases de datos con PVC
  -> ConfigMaps generados desde database/**
  -> cinco Jobs de esquema y datos demo
  -> Config Server y Eureka
  -> microservicios
  -> Gateway
```

Cada nueva ejecucion elimina solo los Jobs completados, los recrea y espera su resultado. No borra los PVC ni duplica los registros demo.

No uses `kubectl apply -f k8s/` como sustituto del script: el Secret y los ConfigMaps de semillas se generan en tiempo de ejecucion y deliberadamente no estan en Git.

## Estado y prueba funcional

```powershell
.\scripts\status-k8s.ps1
.\scripts\verify-k8s.ps1
```

`verify-k8s.ps1` abre temporalmente un port-forward al Gateway y realiza validaciones de solo lectura: salud, CORS, login, cantidades minimas y fallback autenticado al backend principal.

Para acceso manual:

```powershell
kubectl -n clinica-ms port-forward svc/eureka-server 8761:8761
kubectl -n clinica-ms port-forward svc/gateway-service 8090:8090
```

## Detener sin perder datos

El apagado normal escala todos los Deployments a cero y conserva los PVC:

```powershell
.\scripts\stop-k8s.ps1
```

El siguiente arranque restaura las replicas desde los manifiestos.

Para destruir expresamente el namespace, los PVC y los datos locales:

```powershell
.\scripts\stop-k8s.ps1 -Destroy
```
