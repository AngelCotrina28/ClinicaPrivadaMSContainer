# Credenciales demo

Este archivo contiene solo accesos de la aplicacion autorizados para la evaluacion. No incluye credenciales de bases de datos, JWT ni infraestructura.

## Acceso local

- Frontend: `http://localhost:4200`
- Gateway: `http://localhost:8090`
- Login API: `http://localhost:8090/api/auth/login`

| Perfil | Username | Password local |
| --- | --- | --- |
| Administrador | `admin` | `ClinicaAdminLocal123!` |
| Recepcionista | `recepcionista` | `ClinicaDemoLocal123!` |
| Jefe de enfermeria | `jefe_enfermeria` | `ClinicaDemoLocal123!` |
| Enfermero | `enfermero` | `ClinicaDemoLocal123!` |
| Medico | `medico` | `ClinicaDemoLocal123!` |
| Tecnico de farmacia | `tecnico_farmacia` | `ClinicaDemoLocal123!` |
| Cajero | `cajero` | `ClinicaDemoLocal123!` |

Los valores anteriores pertenecen al perfil de demostracion local. Se pueden cambiar en `.env` mediante `SYSTEM_ADMIN_PASSWORD` y `DEMO_USER_PASSWORD`. El sembrado no modifica la clave de una cuenta que ya exista.

## Despliegue

- Frontend: `https://clinica-privada-frontend.vercel.app`
- Gateway: `https://clinica-gateway-service.onrender.com`
- Login API: `https://clinica-gateway-service.onrender.com/api/auth/login`

| Perfil | Username | Password de evaluacion |
| --- | --- | --- |
| Administrador | `admin` | `Admin123` |
| Recepcionista | `demo_recepcionista` | `ClinicaDemoLocal123!` |
| Jefe de enfermeria | `demo_jefe_enfermeria` | `ClinicaDemoLocal123!` |
| Enfermero | `demo_enfermero` | `ClinicaDemoLocal123!` |
| Medico | `demo_medico` | `ClinicaDemoLocal123!` |
| Tecnico de farmacia | `demo_tecnico_farmacia` | `ClinicaDemoLocal123!` |
| Cajero | `demo_cajero` | `ClinicaDemoLocal123!` |

Las siete cuentas fueron verificadas mediante el Gateway el 18 de julio de 2026: cada login devolvio el perfil y rol esperados. `scripts/seed-render-demo.ps1` permite repetir esta comprobacion sin imprimir tokens y conserva el sembrado remoto idempotente. `DEMO_DATA_ENABLED` permanece en `false` en Render para impedir que el despliegue cree cuentas adicionales automaticamente.
