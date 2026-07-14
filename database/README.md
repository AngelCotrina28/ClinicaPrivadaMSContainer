# Database schemas and sample data

This directory contains the database artifacts required to reproduce the data
model without depending on Hibernate automatic schema generation.

| Engine | Microservice | Database | Schema | Sample data |
| --- | --- | --- | --- | --- |
| MySQL | Auth Service | `clinica_auth_db` | `mysql/auth/schema.sql` | `mysql/auth/data.sql` |
| MySQL | Caja Facturacion Service | `clinica_caja_db` | `mysql/caja/schema.sql` | `mysql/caja/data.sql` |
| PostgreSQL | Atencion Medica Service | `clinica_atencion_db` | `postgresql/atencion/schema.sql` | `postgresql/atencion/data.sql` |
| PostgreSQL | Notificaciones Service | `clinica_notificaciones_db` | `postgresql/notificaciones/schema.sql` | `postgresql/notificaciones/data.sql` |
| MongoDB | Citas Service | `clinica_citas_db` | `mongodb/citas/schema.mongodb.js` | `mongodb/citas/data.mongodb.js` |

The SQL and Mongo scripts are idempotent: they can be executed more than once
without duplicating the sample records. In normal local execution, Spring Data
and Hibernate create or update the same structures. These files are the explicit
versioned representation requested for the project deliverable.

The Auth administrator is created by `AuthSeedService` using environment
variables. Its password is intentionally not stored in these SQL files.
