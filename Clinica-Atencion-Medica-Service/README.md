# Clinica Atencion Medica Service

Microservicio de negocio para registrar atenciones medicas, diagnosticos, tratamientos e historial clinico.

## Responsabilidad

- Registrar una atencion medica.
- Consultar atenciones por historia clinica, paciente o medico.
- Cerrar una atencion con tratamiento e indicaciones.
- Anular una atencion registrada.

## Base de datos propia

Usa PostgreSQL.

```text
clinica_atencion_db
```

Tabla principal:

- `atenciones_medicas`

## Endpoints principales

```text
POST  /api/atenciones
GET   /api/atenciones/{id}
GET   /api/atenciones/historia/{historiaClinicaId}
GET   /api/atenciones/paciente/{pacienteId}
GET   /api/atenciones/medico/{medicoId}
PATCH /api/atenciones/{id}/cerrar
PATCH /api/atenciones/{id}/anular
GET   /actuator/health
```

## Clasificacion

Es un microservicio de negocio porque gestiona el acto clinico central: la atencion medica del paciente.
