INSERT INTO notificaciones (
  id, destinatario_tipo, destinatario_id, tipo, canal, estado,
  titulo, mensaje, referencia_tipo, referencia_id, created_at, updated_at
)
SELECT
  9001, 'PACIENTE', 1, 'CITA', 'APP', 'PENDIENTE',
  'Cita confirmada', 'Tu cita fue registrada correctamente.',
  'CITA', 1001, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM notificaciones WHERE id = 9001);

INSERT INTO notificaciones (
  id, destinatario_tipo, destinatario_id, tipo, canal, estado,
  titulo, mensaje, referencia_tipo, referencia_id,
  fecha_envio, fecha_lectura, created_at, updated_at
)
SELECT 9002, 'PACIENTE', 1, 'PAGO', 'EMAIL', 'ENVIADA',
  'Pago registrado', 'Tu pago de demostracion fue registrado.',
  'PAGO', 9101, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM notificaciones WHERE id = 9002);

INSERT INTO notificaciones (
  id, destinatario_tipo, destinatario_id, tipo, canal, estado,
  titulo, mensaje, referencia_tipo, referencia_id,
  fecha_envio, fecha_lectura, created_at, updated_at
)
SELECT 9003, 'PACIENTE', 2, 'CITA', 'APP', 'LEIDA',
  'Recordatorio de cita', 'Recuerda asistir quince minutos antes.',
  'CITA', 9002, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM notificaciones WHERE id = 9003);

INSERT INTO notificaciones (
  id, destinatario_tipo, destinatario_id, tipo, canal, estado,
  titulo, mensaje, referencia_tipo, referencia_id, created_at, updated_at
)
SELECT 9004, 'MEDICO', 12, 'SISTEMA', 'APP', 'PENDIENTE',
  'Atencion pendiente', 'Tienes una atencion medica por completar.',
  'ATENCION', 9003, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM notificaciones WHERE id = 9004);

INSERT INTO notificaciones (
  id, destinatario_tipo, destinatario_id, tipo, canal, estado,
  titulo, mensaje, referencia_tipo, referencia_id,
  fecha_envio, created_at, updated_at
)
SELECT 9005, 'PACIENTE', 3, 'PAGO', 'SMS', 'ERROR',
  'Aviso de pago', 'No se pudo entregar el aviso de pago demo.',
  'PAGO', 9104, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM notificaciones WHERE id = 9005);

INSERT INTO notificaciones (
  id, destinatario_tipo, destinatario_id, tipo, canal, estado,
  titulo, mensaje, referencia_tipo, referencia_id,
  fecha_envio, created_at, updated_at
)
SELECT 9006, 'PACIENTE', 4, 'RECETA', 'EMAIL', 'ENVIADA',
  'Receta disponible', 'Tu receta ya esta disponible en farmacia.',
  'RECETA', 9005, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM notificaciones WHERE id = 9006);

-- Corrige el enum de la notificacion demo si fue creada con una semilla antigua.
UPDATE notificaciones
SET tipo = 'SISTEMA', referencia_tipo = 'ATENCION', updated_at = CURRENT_TIMESTAMP
WHERE id = 9004;

SELECT setval(
  pg_get_serial_sequence('notificaciones', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM notificaciones), 1),
  true
);
