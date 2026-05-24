-- mysql-workbench-setup.sql
-- Basic MySQL setup for PepitoBuscaError.
-- Execute this file in MySQL Workbench with an administrator user, for example root.

CREATE DATABASE IF NOT EXISTS pepito_busca_error
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

DROP USER IF EXISTS 'pepito_app'@'localhost';

CREATE USER 'pepito_app'@'localhost'
  IDENTIFIED BY 'change_this_password';

GRANT ALL PRIVILEGES ON pepito_busca_error.* TO 'pepito_app'@'localhost';
FLUSH PRIVILEGES;

USE pepito_busca_error;

CREATE TABLE IF NOT EXISTS companies (
  id_company BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(140) NOT NULL,
  domain VARCHAR(255) NOT NULL,
  corporate_email VARCHAR(255) NOT NULL,
  sector VARCHAR(120) NOT NULL,
  registration_date DATETIME(6) NOT NULL,
  PRIMARY KEY (id_company),
  INDEX idx_companies_domain (domain),
  INDEX idx_companies_sector (sector)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS analyses (
  id_analysis BIGINT NOT NULL AUTO_INCREMENT,
  id_company BIGINT NOT NULL,
  analysis_date DATETIME(6) NOT NULL,
  risk_score INT NOT NULL,
  risk_level VARCHAR(20) NOT NULL,
  status VARCHAR(40) NOT NULL,
  PRIMARY KEY (id_analysis),
  INDEX idx_analyses_company (id_company),
  INDEX idx_analyses_risk_level (risk_level),
  INDEX idx_analyses_date (analysis_date),
  CONSTRAINT fk_analyses_company
    FOREIGN KEY (id_company) REFERENCES companies (id_company)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS indicators (
  id_indicator BIGINT NOT NULL AUTO_INCREMENT,
  id_analysis BIGINT NOT NULL,
  type VARCHAR(100) NOT NULL,
  indicator_value VARCHAR(180) NOT NULL,
  description LONGTEXT NOT NULL,
  severity VARCHAR(20) NOT NULL,
  PRIMARY KEY (id_indicator),
  INDEX idx_indicators_analysis (id_analysis),
  INDEX idx_indicators_severity (severity),
  CONSTRAINT fk_indicators_analysis
    FOREIGN KEY (id_analysis) REFERENCES analyses (id_analysis)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS recommendations (
  id_recommendation BIGINT NOT NULL AUTO_INCREMENT,
  id_analysis BIGINT NOT NULL,
  priority VARCHAR(20) NOT NULL,
  description LONGTEXT NOT NULL,
  action LONGTEXT NOT NULL,
  PRIMARY KEY (id_recommendation),
  INDEX idx_recommendations_analysis (id_analysis),
  INDEX idx_recommendations_priority (priority),
  CONSTRAINT fk_recommendations_analysis
    FOREIGN KEY (id_analysis) REFERENCES analyses (id_analysis)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tracked_devices (
  id_device BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  device_type VARCHAR(80) NOT NULL,
  owner VARCHAR(120) NOT NULL,
  latitude DOUBLE NULL,
  longitude DOUBLE NULL,
  accuracy_meters DOUBLE NULL,
  location_label VARCHAR(180) NULL,
  last_client_ip VARCHAR(45) NULL,
  last_user_agent VARCHAR(255) NULL,
  notes VARCHAR(500) NULL,
  active BIT NOT NULL,
  tracking_token VARCHAR(80) NULL,
  registered_at DATETIME(6) NOT NULL,
  last_seen_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id_device),
  UNIQUE KEY uk_tracked_devices_token (tracking_token),
  INDEX idx_tracked_devices_owner (owner),
  INDEX idx_tracked_devices_last_seen (last_seen_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
  INDEX idx_scan_run_target (target_id),
  INDEX idx_scan_run_started (started_at),
  CONSTRAINT fk_scan_run_target
    FOREIGN KEY (target_id) REFERENCES audit_target (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS finding (
  id BIGINT NOT NULL AUTO_INCREMENT,
  scan_run_id BIGINT NOT NULL,
  category VARCHAR(40) NOT NULL,
  severity VARCHAR(40) NOT NULL,
  status VARCHAR(40) NULL DEFAULT 'OPEN',
  title VARCHAR(180) NOT NULL,
  evidence LONGTEXT NOT NULL,
  recommendation LONGTEXT NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_finding_scan_run (scan_run_id),
  INDEX idx_finding_severity (severity),
  CONSTRAINT fk_finding_scan_run
    FOREIGN KEY (scan_run_id) REFERENCES scan_run (id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SHOW DATABASES LIKE 'pepito_busca_error';
SHOW TABLES;
SELECT user, host FROM mysql.user WHERE user = 'pepito_app';
