# Clinica Privada - Microservicios

Contenedor Maven de la arquitectura de microservicios de la Clinica Privada. Incluye los servicios, cinco bases de datos independientes, Docker Compose, Kubernetes, datos demo y verificaciones funcionales.

**Acceso para la evaluacion:** consulta [`CREDENCIALES_DEMO.md`](CREDENCIALES_DEMO.md) para los usuarios locales por rol y las URLs de ingreso.

## Arquitectura

| Tipo | Servicio | Puerto | Base de datos |
| --- | --- | ---: | --- |
| Infraestructura | Config Server | 8888 | No aplica |
| Infraestructura | Eureka Server | 8761 | No aplica |
| Infraestructura | Auth Service | 8091 | MySQL |
| Apoyo | Notificaciones Service | 8092 | PostgreSQL |
| Negocio | Citas Service | 8093 | MongoDB |
| Negocio | Atencion Medica Service | 8094 | PostgreSQL |
| Negocio | Caja Facturacion Service | 8095 | MySQL |
| Infraestructura | Gateway Service | 8090 | No aplica |

El Gateway es la entrada unica. Las rutas `/api/ms/**` apuntan expresamente a los microservicios nuevos. Las rutas heredadas de citas, atencion y caja conservan compatibilidad con el backend principal durante la migracion; por eso sus tres banderas `*_LEGACY_ROUTE_ENABLED` deben permanecer en `false` mientras el frontend siga usando esos contratos.

## Inicio rapido con Docker Compose

Requisitos: Docker Desktop, PowerShell y puertos 3307, 3308, 5433, 5434, 27018, 8090-8095, 8761 y 8888 disponibles.

Si Windows bloquea scripts en la terminal actual, habilitalos solo para ese proceso (no requiere cambiar la politica global):

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

```powershell
.\scripts\start-microservices.ps1
.\scripts\verify-microservices.ps1
```

La verificacion predeterminada es de solo lectura: salud, CORS, login, cantidades demo y fallback autenticado al backend principal. La prueba opcional siguiente ejerce POST/PATCH y por tanto crea registros transitorios:

```powershell
.\scripts\verify-microservices.ps1 -ExerciseWrites
```

El primer comando:

1. Crea `.env` desde `.env.example` si no existe y valida todas las variables obligatorias.
2. Construye las imagenes.
3. Inicia las cinco bases de datos.
4. Ejecuta una semilla idempotente por base de datos.
5. Inicia Config Server, Eureka, los microservicios y el Gateway.

No hace falta ejecutar SQL manualmente. Las semillas dejan como minimo 7 roles, 7 citas, 4 atenciones, 5 deudas, 4 pagos y 6 notificaciones. Repetir el arranque no duplica esos registros.

Para detener los contenedores conservando los volumenes:

```powershell
.\scripts\stop-microservices.ps1
```

Puertos de bases de datos expuestos en el host:

| Motor | Servicio | Host | Contenedor |
| --- | --- | ---: | ---: |
| MySQL | Auth | 3307 | 3306 |
| MySQL | Caja | 3308 | 3306 |
| PostgreSQL | Atencion | 5433 | 5432 |
| PostgreSQL | Notificaciones | 5434 | 5432 |
| MongoDB | Citas | 27018 | 27017 |

## Kubernetes local

Habilita Kubernetes en Docker Desktop y ejecuta:

```powershell
.\scripts\start-k8s.ps1
.\scripts\status-k8s.ps1
.\scripts\verify-k8s.ps1
```

`start-k8s.ps1` construye o reutiliza las imagenes locales, crea el Secret desde `.env`, genera ConfigMaps desde `database/**`, espera las bases, ejecuta cinco Jobs de semilla y finalmente inicia la plataforma. Se puede repetir: los Jobs se recrean y los datos demo siguen siendo idempotentes.

El apagado normal conserva los PVC:

```powershell
.\scripts\stop-k8s.ps1
```

Solo para borrar el entorno y sus datos locales:

```powershell
.\scripts\stop-k8s.ps1 -Destroy
```

## Variables de entorno

- `.env.example` contiene valores publicos exclusivos para desarrollo local.
- `.env` es creado automaticamente, se ignora en Git y puede personalizarse sin modificar manifiestos.
- Los seis servicios de aplicacion que requieren credenciales o endpoints tienen su propio `.env.example`; Config Server y Eureka usan valores locales por defecto.
- Las credenciales desplegadas no se versionan ni se reemplazan. En Render, las variables marcadas `sync: false` deben existir en el panel del servicio.
- La misma `JWT_SECRET` debe usarse en el backend principal y en todos los servicios que validan JWT.

Los ambientes Postman local y Render incluyen sus respectivas cuentas publicas de evaluacion y se pueden importar y ejecutar. No contienen credenciales de bases de datos, JWT ni infraestructura.

Con `DEMO_DATA_ENABLED=true`, Auth crea sin sobrescribir cuentas existentes los usuarios `recepcionista`, `jefe_enfermeria`, `enfermero`, `medico`, `tecnico_farmacia` y `cajero`. Todos usan localmente el valor de `DEMO_USER_PASSWORD`; Render mantiene esta opcion desactivada.

## Compatibilidad con el frontend y backend principal

Para probar el frontend completo durante la migracion, inicia tambien el backend principal en el puerto 8080. Desde Docker el Gateway lo alcanza mediante `BACKEND_URL=http://host.docker.internal:8080`. Auth y Notificaciones ya se enrutan directamente; las rutas `/api/ms/citas`, `/api/ms/atenciones` y `/api/ms/caja` permiten demostrar los servicios nuevos sin cambiar los contratos heredados del frontend.

## Validacion de codigo

Desde la raiz:

```powershell
.\mvnw.cmd test
docker compose --env-file .env.example -f docker-compose.microservices.yml config --quiet
```

Los detalles de datos, Kubernetes y Postman estan en `database/README.md`, `k8s/README.md` y `postman/README.md`.

## Semilla controlada de Render

`scripts/seed-render-demo.ps1` existe para reconstruir los datos demo desplegados si fuera necesario. Consulta antes de cada POST, no borra ni sobrescribe y no reintenta escrituras a ciegas. Al final valida el login y perfil de las siete cuentas publicadas. Muta Render de forma idempotente y no se usa para el arranque local.

```powershell
$adminPassword = Read-Host "Password del administrador de Render" -AsSecureString
.\scripts\seed-render-demo.ps1 -AdminPassword $adminPassword
```

El script recibe `AdminPassword` en tiempo de ejecucion, no lo incluye en su codigo y nunca imprime el token.
