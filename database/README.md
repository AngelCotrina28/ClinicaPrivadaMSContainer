# Esquemas y datos demo

Esta carpeta es la fuente versionada de los esquemas y datos iniciales de cada microservicio.

| Motor | Microservicio | Base | Esquema | Datos |
| --- | --- | --- | --- | --- |
| MySQL | Auth | `clinica_auth_db` | `mysql/auth/schema.sql` | `mysql/auth/data.sql` |
| MySQL | Caja | `clinica_caja_db` | `mysql/caja/schema.sql` | `mysql/caja/data.sql` |
| PostgreSQL | Atencion | `clinica_atencion_db` | `postgresql/atencion/schema.sql` | `postgresql/atencion/data.sql` |
| PostgreSQL | Notificaciones | `clinica_notificaciones_db` | `postgresql/notificaciones/schema.sql` | `postgresql/notificaciones/data.sql` |
| MongoDB | Citas | `clinica_citas_db` | `mongodb/citas/schema.mongodb.js` | `mongodb/citas/data.mongodb.js` |

Docker Compose monta estos archivos en cinco contenedores de semilla de una sola ejecucion. Kubernetes genera cinco ConfigMaps y ejecuta cinco Jobs equivalentes. En ambos casos los servicios de aplicacion esperan a que la semilla termine correctamente.

Los scripts son idempotentes: se pueden ejecutar varias veces sin duplicar los IDs demo. El resultado minimo es:

- 7 roles canonicos: `ADMINISTRADOR`, `RECEPCIONISTA`, `MEDICO`, `CAJERO`, `JEFE_ENFERMERIA`, `ENFERMERO` y `TECNICO_FARMACIA`.
- 7 citas en distintos estados.
- 4 atenciones medicas.
- 5 deudas y 4 pagos coherentes con sus saldos.
- 6 notificaciones con estados validos.

El administrador no se inserta en SQL porque su contrasena debe convertirse a BCrypt. `AuthSeedService` lo crea desde `SYSTEM_ADMIN_PASSWORD`. La API de Notificaciones crea nuevas notificaciones como `ENVIADA`; el estado `LEIDA` se alcanza mediante el endpoint PATCH correspondiente. Las filas demo incluyen otros estados validos para comprobar el resumen.
