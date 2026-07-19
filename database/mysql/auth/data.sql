USE clinica_auth_db;

INSERT INTO roles (nombre, descripcion, activo) VALUES
  ('ADMINISTRADOR', 'Gestiona usuarios, roles y configuracion del sistema.', TRUE),
  ('RECEPCIONISTA', 'Gestiona admision y citas.', TRUE),
  ('MEDICO', 'Gestiona atenciones medicas.', TRUE),
  ('CAJERO', 'Gestiona pagos, comprobantes y caja.', TRUE),
  ('JEFE_ENFERMERIA', 'Coordina al personal de enfermeria.', TRUE),
  ('ENFERMERO', 'Registra y ejecuta cuidados de enfermeria.', TRUE),
  ('TECNICO_FARMACIA', 'Gestiona recetas e inventario de farmacia.', TRUE)
ON DUPLICATE KEY UPDATE
  descripcion = VALUES(descripcion),
  activo = VALUES(activo);

-- AuthSeedService creates the administrator with a BCrypt hash obtained from
-- SYSTEM_ADMIN_PASSWORD. No plain-text password is versioned here.
