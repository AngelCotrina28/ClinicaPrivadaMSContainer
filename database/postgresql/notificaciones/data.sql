INSERT INTO notificaciones (
  id, destinatario_tipo, destinatario_id, tipo, canal, estado,
  titulo, mensaje, referencia_tipo, referencia_id, created_at, updated_at
)
SELECT
  9001, 'PACIENTE', 1, 'CITA', 'APP', 'PENDIENTE',
  'Cita confirmada', 'Tu cita fue registrada correctamente.',
  'CITA', 1001, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM notificaciones WHERE id = 9001);

SELECT setval(
  pg_get_serial_sequence('notificaciones', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM notificaciones), 1),
  true
);
