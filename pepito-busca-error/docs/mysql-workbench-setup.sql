-- mysql-workbench-setup.sql
-- Script de configuración para PepitoBuscaError
-- Ejecutar en MySQL Workbench con un usuario administrador, por ejemplo root.

CREATE DATABASE IF NOT EXISTS pepito_busca_error
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Recreamos el usuario para evitar errores si ya existía con otra contraseña.
DROP USER IF EXISTS 'pepito_app'@'localhost';

CREATE USER 'pepito_app'@'localhost'
  IDENTIFIED BY 'change_this_password';

GRANT ALL PRIVILEGES ON pepito_busca_error.* TO 'pepito_app'@'localhost';

FLUSH PRIVILEGES;

USE pepito_busca_error;

CREATE TABLE IF NOT EXISTS audit_target (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(140) NOT NULL,
  domain VARCHAR(255) NOT NULL,
  url VARCHAR(500) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_audit_target_domain (domain)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS scan_run (
  id BIGINT NOT NULL AUTO_INCREMENT,
  target_id BIGINT NOT NULL,
  started_at DATETIME(6) NOT NULL,
  completed_at DATETIME(6) NULL,
  status VARCHAR(40) NOT NULL,
  risk_score INT NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_scan_run_started_at (started_at),
  INDEX idx_scan_run_target_id (target_id),
  CONSTRAINT fk_scan_run_target
    FOREIGN KEY (target_id) REFERENCES audit_target (id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS finding (
  id BIGINT NOT NULL AUTO_INCREMENT,
  scan_run_id BIGINT NOT NULL,
  category VARCHAR(40) NOT NULL,
  severity VARCHAR(40) NOT NULL,
  title VARCHAR(180) NOT NULL,
  evidence LONGTEXT NOT NULL,
  recommendation LONGTEXT NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_finding_scan_run_id (scan_run_id),
  INDEX idx_finding_severity (severity),
  INDEX idx_finding_category (category),
  CONSTRAINT fk_finding_scan_run
    FOREIGN KEY (scan_run_id) REFERENCES scan_run (id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Comprobaciones finales
SHOW DATABASES LIKE 'pepito_busca_error';
SHOW TABLES;
SELECT user, host FROM mysql.user WHERE user = 'pepito_app';
