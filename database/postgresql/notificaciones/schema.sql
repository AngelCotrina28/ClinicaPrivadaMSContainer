CREATE TABLE IF NOT EXISTS notificaciones (
  id BIGSERIAL PRIMARY KEY,
  destinatario_tipo VARCHAR(40) NOT NULL,
  destinatario_id BIGINT NOT NULL,
  tipo VARCHAR(30) NOT NULL,
  canal VARCHAR(30) NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
  titulo VARCHAR(140) NOT NULL,
  mensaje TEXT NOT NULL,
  referencia_tipo VARCHAR(50),
  referencia_id BIGINT,
  fecha_programada TIMESTAMP,
  fecha_envio TIMESTAMP,
  fecha_lectura TIMESTAMP,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notif_destinatario
  ON notificaciones (destinatario_tipo, destinatario_id);
CREATE INDEX IF NOT EXISTS idx_notif_estado
  ON notificaciones (estado);
CREATE INDEX IF NOT EXISTS idx_notif_referencia
  ON notificaciones (referencia_tipo, referencia_id);
