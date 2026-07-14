USE clinica_caja_db;

INSERT INTO deudas (
  id, paciente_id, concepto, referencia_tipo, referencia_id,
  monto_total, monto_pagado, estado, created_at, updated_at
)
SELECT
  9001, 1, 'Consulta medica de demostracion', 'ATENCION', 'DEMO-ATENCION-1',
  80.00, 0.00, 'PENDIENTE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM deudas WHERE id = 9001);
