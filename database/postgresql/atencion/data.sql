INSERT INTO atenciones_medicas (
  id, historia_clinica_id, paciente_id, medico_id, cita_id,
  fecha_atencion, motivo_consulta, diagnostico, tratamiento,
  indicaciones_receta, estado, created_at, updated_at
)
SELECT
  9001, 1, 1, 10, '64b000000000000000009001', CURRENT_TIMESTAMP,
  'Dolor abdominal', 'Gastritis probable',
  'Control dietario y medicacion indicada',
  'Tomar medicamento despues de alimentos',
  'REGISTRADA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM atenciones_medicas WHERE id = 9001);

INSERT INTO atenciones_medicas (
  id, historia_clinica_id, paciente_id, medico_id, cita_id,
  fecha_atencion, motivo_consulta, diagnostico, tratamiento,
  indicaciones_receta, estado, created_at, updated_at
)
SELECT 9002, 2, 2, 11, '64b000000000000000009003', CURRENT_TIMESTAMP,
  'Control pediatrico', 'Paciente estable y con desarrollo adecuado',
  'Continuar controles preventivos', 'Control en tres meses',
  'CERRADA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM atenciones_medicas WHERE id = 9002);

INSERT INTO atenciones_medicas (
  id, historia_clinica_id, paciente_id, medico_id, cita_id,
  fecha_atencion, motivo_consulta, diagnostico, tratamiento,
  indicaciones_receta, estado, created_at, updated_at
)
SELECT 9003, 3, 3, 12, '64b000000000000000009004', CURRENT_TIMESTAMP,
  'Dolor lumbar', 'Lumbalgia mecanica',
  'Analgesia y terapia fisica', 'Evitar cargar peso por siete dias',
  'REGISTRADA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM atenciones_medicas WHERE id = 9003);

INSERT INTO atenciones_medicas (
  id, historia_clinica_id, paciente_id, medico_id, cita_id,
  fecha_atencion, motivo_consulta, diagnostico, tratamiento,
  indicaciones_receta, estado, created_at, updated_at
)
SELECT 9004, 4, 4, 13, '64b000000000000000009005', CURRENT_TIMESTAMP,
  'Control de presion arterial', 'Hipertension controlada',
  'Mantener tratamiento habitual', 'Registrar presion diariamente',
  'CERRADA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM atenciones_medicas WHERE id = 9004);

-- Converge instalaciones creadas con semillas anteriores. Los IDs 9001-9004
-- estan reservados exclusivamente para la demostracion local.
UPDATE atenciones_medicas
SET cita_id = CASE id
    WHEN 9001 THEN '64b000000000000000009001'
    WHEN 9002 THEN '64b000000000000000009003'
    WHEN 9003 THEN '64b000000000000000009004'
    WHEN 9004 THEN '64b000000000000000009005'
  END,
  updated_at = CURRENT_TIMESTAMP
WHERE id IN (9001, 9002, 9003, 9004);

SELECT setval(
  pg_get_serial_sequence('atenciones_medicas', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM atenciones_medicas), 1),
  true
);
