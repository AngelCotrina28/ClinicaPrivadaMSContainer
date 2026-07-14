CREATE TABLE IF NOT EXISTS atenciones_medicas (
  id BIGSERIAL PRIMARY KEY,
  historia_clinica_id BIGINT NOT NULL,
  paciente_id BIGINT NOT NULL,
  medico_id BIGINT NOT NULL,
  cita_id VARCHAR(80),
  fecha_atencion TIMESTAMP NOT NULL,
  motivo_consulta VARCHAR(300) NOT NULL,
  diagnostico VARCHAR(500) NOT NULL,
  tratamiento VARCHAR(700),
  indicaciones_receta VARCHAR(700),
  estado VARCHAR(30) NOT NULL DEFAULT 'REGISTRADA',
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_atenciones_historia
  ON atenciones_medicas (historia_clinica_id);
CREATE INDEX IF NOT EXISTS idx_atenciones_paciente
  ON atenciones_medicas (paciente_id);
CREATE INDEX IF NOT EXISTS idx_atenciones_medico
  ON atenciones_medicas (medico_id);
