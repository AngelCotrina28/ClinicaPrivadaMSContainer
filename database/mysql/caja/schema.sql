CREATE DATABASE IF NOT EXISTS clinica_caja_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE clinica_caja_db;

CREATE TABLE IF NOT EXISTS deudas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  paciente_id BIGINT NOT NULL,
  concepto VARCHAR(120) NOT NULL,
  referencia_tipo VARCHAR(50),
  referencia_id VARCHAR(80),
  monto_total DECIMAL(12,2) NOT NULL,
  monto_pagado DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
  created_at DATETIME(6),
  updated_at DATETIME(6),
  PRIMARY KEY (id),
  INDEX idx_deudas_paciente (paciente_id),
  INDEX idx_deudas_estado (estado)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS pagos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  deuda_id BIGINT NOT NULL,
  paciente_id BIGINT NOT NULL,
  monto DECIMAL(12,2) NOT NULL,
  metodo_pago VARCHAR(30) NOT NULL,
  numero_comprobante VARCHAR(40) NOT NULL,
  observacion VARCHAR(300),
  fecha_pago DATETIME(6),
  PRIMARY KEY (id),
  INDEX idx_pagos_deuda (deuda_id),
  INDEX idx_pagos_paciente (paciente_id)
) ENGINE=InnoDB;
