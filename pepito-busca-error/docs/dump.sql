-- dump.sql
-- Core academic sample data for companies, analyses, indicators, and recommendations.
-- Other modules can create their tables automatically with spring.jpa.hibernate.ddl-auto=update
-- or through mysql-workbench-setup.sql.

CREATE DATABASE IF NOT EXISTS pepito_busca_error
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE pepito_busca_error;

DROP TABLE IF EXISTS recommendations;
DROP TABLE IF EXISTS indicators;
DROP TABLE IF EXISTS analyses;
DROP TABLE IF EXISTS companies;

CREATE TABLE companies (
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

CREATE TABLE analyses (
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

CREATE TABLE indicators (
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

CREATE TABLE recommendations (
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

INSERT INTO companies (id_company, name, domain, corporate_email, sector, registration_date) VALUES
  (1, 'Northwind Dental Clinic', 'northwind-dental.com', 'it@northwind-dental.com', 'Healthcare', '2026-04-18 09:10:00.000000'),
  (2, 'Blue Harbor Logistics', 'blueharborlogistics.com', 'security@blueharborlogistics.com', 'Logistics', '2026-04-22 11:35:00.000000'),
  (3, 'Aula Nova Academy', 'aulanova.edu', 'admin@aulanova.edu', 'Education', '2026-04-25 13:20:00.000000'),
  (4, 'Mercado Sol Retail', 'mercadosol.es', 'sistemas@mercadosol.es', 'Retail', '2026-05-02 10:45:00.000000'),
  (5, 'Finanzas Prado', 'finanzasprado.com', 'soporte@finanzasprado.com', 'Finance', '2026-05-08 16:05:00.000000');

INSERT INTO analyses (id_analysis, id_company, analysis_date, risk_score, risk_level, status) VALUES
  (1, 1, '2026-05-10 10:00:00.000000', 25, 'LOW', 'COMPLETED'),
  (2, 2, '2026-05-11 12:15:00.000000', 65, 'HIGH', 'COMPLETED'),
  (3, 3, '2026-05-12 09:30:00.000000', 15, 'LOW', 'COMPLETED'),
  (4, 4, '2026-05-13 17:40:00.000000', 50, 'MEDIUM', 'COMPLETED'),
  (5, 5, '2026-05-14 08:55:00.000000', 85, 'CRITICAL', 'COMPLETED'),
  (6, 2, '2026-05-16 15:20:00.000000', 35, 'MEDIUM', 'COMPLETED');

INSERT INTO indicators (id_analysis, type, indicator_value, description, severity) VALUES
  (1, 'Suspicious corporate email', 'Corporate email risk', 'The corporate email shows signs of exposure, spoofing risk, or weak configuration.', 'HIGH'),
  (2, 'Suspicious IP address', 'Suspicious hosting signal', 'The company infrastructure uses an IP address that should be reviewed.', 'CRITICAL'),
  (2, 'Suspicious corporate email', 'Corporate email risk', 'The corporate email shows signs of exposure, spoofing risk, or weak configuration.', 'HIGH'),
  (3, 'Domain without HTTPS', 'HTTP service detected', 'The company domain is not forcing encrypted HTTPS access.', 'MEDIUM'),
  (4, 'Domain without HTTPS', 'HTTP service detected', 'The company domain is not forcing encrypted HTTPS access.', 'MEDIUM'),
  (4, 'Suspicious corporate email', 'Corporate email risk', 'The corporate email shows signs of exposure, spoofing risk, or weak configuration.', 'HIGH'),
  (4, 'Missing security headers', 'Header hardening gap', 'Important browser security headers are missing from the public web response.', 'MEDIUM'),
  (5, 'Suspicious IP address', 'Suspicious hosting signal', 'The company infrastructure uses an IP address that should be reviewed.', 'CRITICAL'),
  (5, 'Suspicious corporate email', 'Corporate email risk', 'The corporate email shows signs of exposure, spoofing risk, or weak configuration.', 'HIGH'),
  (5, 'Domain without HTTPS', 'HTTP service detected', 'The company domain is not forcing encrypted HTTPS access.', 'MEDIUM'),
  (5, 'Missing security headers', 'Header hardening gap', 'Important browser security headers are missing from the public web response.', 'MEDIUM'),
  (5, 'Invalid SSL certificate', 'Certificate problem', 'The SSL certificate is expired, invalid, or not trusted by browsers.', 'HIGH'),
  (6, 'Domain without HTTPS', 'HTTP service detected', 'The company domain is not forcing encrypted HTTPS access.', 'MEDIUM'),
  (6, 'Missing security headers', 'Header hardening gap', 'Important browser security headers are missing from the public web response.', 'MEDIUM'),
  (6, 'Invalid SSL certificate', 'Certificate problem', 'The SSL certificate is expired, invalid, or not trusted by browsers.', 'HIGH');

INSERT INTO recommendations (id_analysis, priority, description, action) VALUES
  (1, 'HIGH', 'Change exposed credentials.', 'Reset suspicious mailbox credentials and review SPF, DKIM, and DMARC configuration.'),
  (2, 'HIGH', 'Review IP reputation.', 'Check the affected IP address, hosting provider, DNS records, and firewall exposure.'),
  (2, 'HIGH', 'Change exposed credentials.', 'Reset suspicious mailbox credentials and review SPF, DKIM, and DMARC configuration.'),
  (3, 'HIGH', 'Enable HTTPS.', 'Install a valid TLS certificate and redirect all HTTP traffic to HTTPS.'),
  (4, 'HIGH', 'Enable HTTPS.', 'Install a valid TLS certificate and redirect all HTTP traffic to HTTPS.'),
  (4, 'HIGH', 'Change exposed credentials.', 'Reset suspicious mailbox credentials and review SPF, DKIM, and DMARC configuration.'),
  (4, 'MEDIUM', 'Review security headers.', 'Add HSTS, Content-Security-Policy, X-Content-Type-Options, Referrer-Policy, and related headers.'),
  (5, 'HIGH', 'Review IP reputation.', 'Check the affected IP address, hosting provider, DNS records, and firewall exposure.'),
  (5, 'HIGH', 'Change exposed credentials.', 'Reset suspicious mailbox credentials and review SPF, DKIM, and DMARC configuration.'),
  (5, 'HIGH', 'Enable HTTPS.', 'Install a valid TLS certificate and redirect all HTTP traffic to HTTPS.'),
  (5, 'MEDIUM', 'Review security headers.', 'Add HSTS, Content-Security-Policy, X-Content-Type-Options, Referrer-Policy, and related headers.'),
  (5, 'HIGH', 'Renew or install SSL certificate.', 'Replace expired or invalid certificates and verify the complete certificate chain.'),
  (6, 'HIGH', 'Enable HTTPS.', 'Install a valid TLS certificate and redirect all HTTP traffic to HTTPS.'),
  (6, 'MEDIUM', 'Review security headers.', 'Add HSTS, Content-Security-Policy, X-Content-Type-Options, Referrer-Policy, and related headers.'),
  (6, 'HIGH', 'Renew or install SSL certificate.', 'Replace expired or invalid certificates and verify the complete certificate chain.');
