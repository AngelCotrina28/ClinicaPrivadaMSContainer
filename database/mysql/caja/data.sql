USE clinica_caja_db;

INSERT INTO deudas (
  id, paciente_id, concepto, referencia_tipo, referencia_id,
  monto_total, monto_pagado, estado, created_at, updated_at
)
SELECT
  9001, 1, 'Consulta medica de demostracion', 'ATENCION', '9001',
  80.00, 80.00, 'PAGADA', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM deudas WHERE id = 9001);

INSERT INTO deudas (
  id, paciente_id, concepto, referencia_tipo, referencia_id,
  monto_total, monto_pagado, estado, created_at, updated_at
)
SELECT 9002, 1, 'Analisis de laboratorio', 'ORDEN_SERVICIO', 'DEMO-ORDEN-2',
  120.00, 50.00, 'PENDIENTE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM deudas WHERE id = 9002);

INSERT INTO deudas (
  id, paciente_id, concepto, referencia_tipo, referencia_id,
  monto_total, monto_pagado, estado, created_at, updated_at
)
SELECT 9003, 2, 'Consulta pediatrica', 'ATENCION', '9002',
  60.00, 60.00, 'PAGADA', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM deudas WHERE id = 9003);

INSERT INTO deudas (
  id, paciente_id, concepto, referencia_tipo, referencia_id,
  monto_total, monto_pagado, estado, created_at, updated_at
)
SELECT 9004, 3, 'Procedimiento ambulatorio', 'ORDEN_SERVICIO', 'DEMO-ORDEN-4',
  200.00, 100.00, 'PENDIENTE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM deudas WHERE id = 9004);

INSERT INTO deudas (
  id, paciente_id, concepto, referencia_tipo, referencia_id,
  monto_total, monto_pagado, estado, created_at, updated_at
)
SELECT 9005, 4, 'Medicamentos de farmacia', 'RECETA', 'DEMO-RECETA-5',
  45.00, 0.00, 'PENDIENTE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM deudas WHERE id = 9005);

INSERT INTO pagos (
  id, deuda_id, paciente_id, monto, metodo_pago,
  numero_comprobante, observacion, fecha_pago
)
SELECT 9101, 9001, 1, 80.00, 'EFECTIVO', 'DEMO-B001-0001',
  'Pago completo de consulta demo', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM pagos WHERE id = 9101);

INSERT INTO pagos (
  id, deuda_id, paciente_id, monto, metodo_pago,
  numero_comprobante, observacion, fecha_pago
)
SELECT 9102, 9002, 1, 50.00, 'TARJETA', 'DEMO-B001-0002',
  'Adelanto de laboratorio demo', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM pagos WHERE id = 9102);

INSERT INTO pagos (
  id, deuda_id, paciente_id, monto, metodo_pago,
  numero_comprobante, observacion, fecha_pago
)
SELECT 9103, 9003, 2, 60.00, 'YAPE', 'DEMO-B001-0003',
  'Pago completo de pediatria demo', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM pagos WHERE id = 9103);

INSERT INTO pagos (
  id, deuda_id, paciente_id, monto, metodo_pago,
  numero_comprobante, observacion, fecha_pago
)
SELECT 9104, 9004, 3, 100.00, 'TRANSFERENCIA', 'DEMO-B001-0004',
  'Adelanto de procedimiento demo', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM pagos WHERE id = 9104);

-- Converge los registros demo reservados que pudieron ser creados por una
-- version anterior de la semilla, sin tocar las operaciones reales del usuario.
UPDATE deudas
SET referencia_id = '9001', monto_pagado = 80.00, estado = 'PAGADA', updated_at = CURRENT_TIMESTAMP(6)
WHERE id = 9001;

UPDATE deudas
SET referencia_id = '9002', monto_pagado = 60.00, estado = 'PAGADA', updated_at = CURRENT_TIMESTAMP(6)
WHERE id = 9003;
