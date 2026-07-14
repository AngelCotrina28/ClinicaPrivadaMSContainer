INSERT INTO atenciones_medicas (
  id, historia_clinica_id, paciente_id, medico_id, cita_id,
  fecha_atencion, motivo_consulta, diagnostico, tratamiento,
  indicaciones_receta, estado, created_at, updated_at
)
SELECT
  9001, 1, 1, 10, 'DEMO-CITA-1', CURRENT_TIMESTAMP,
  'Dolor abdominal', 'Gastritis probable',
  'Control dietario y medicacion indicada',
  'Tomar medicamento despues de alimentos',
  'REGISTRADA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM atenciones_medicas WHERE id = 9001);

SELECT setval(
  pg_get_serial_sequence('atenciones_medicas', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM atenciones_medicas), 1),
  true
);
