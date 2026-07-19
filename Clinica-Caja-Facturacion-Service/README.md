# Clinica Caja Facturacion Service

Microservicio de negocio para deudas, pagos, comprobantes y resumen de caja.

## Responsabilidad

- Crear deudas de pacientes.
- Consultar deudas por paciente o estado.
- Registrar pagos.
- Generar numero de comprobante interno.
- Consultar resumen de caja.

## Base de datos propia

Usa MySQL.

```text
clinica_caja_db
```

Tablas principales:

- `deudas`
- `pagos`

## Ejecucion local recomendada

Desde la raiz, `.\scripts\start-microservices.ps1` carga el `.env`, inicia MySQL, ejecuta la semilla y arranca el servicio en `http://localhost:8095`.

## Endpoints principales

```text
POST /api/caja/deudas
GET  /api/caja/deudas
GET  /api/caja/deudas/{id}
POST /api/caja/pagos
GET  /api/caja/pagos/{id}
GET  /api/caja/pagos/paciente/{pacienteId}
GET  /api/caja/resumen
GET  /actuator/health
```

## Clasificacion

Es un microservicio de negocio porque gestiona el proceso economico de la clinica: deudas, pagos y facturacion.
