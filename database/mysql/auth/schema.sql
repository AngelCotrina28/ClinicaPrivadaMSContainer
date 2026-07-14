CREATE DATABASE IF NOT EXISTS clinica_auth_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE clinica_auth_db;

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  descripcion VARCHAR(200),
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  CONSTRAINT uk_roles_nombre UNIQUE (nombre)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(60) NOT NULL,
  email VARCHAR(120) NOT NULL,
  nombre_completo VARCHAR(120) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  rol_id BIGINT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME(6),
  updated_at DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_usuarios_username UNIQUE (username),
  CONSTRAINT uk_usuarios_email UNIQUE (email),
  CONSTRAINT fk_usuarios_roles FOREIGN KEY (rol_id) REFERENCES roles (id)
) ENGINE=InnoDB;
