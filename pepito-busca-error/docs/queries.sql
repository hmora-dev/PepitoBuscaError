-- Useful SELECT queries for PepitoBuscaError.

USE pepito_busca_error;

-- 1. List companies with their analyses.
SELECT
  c.id_company,
  c.name,
  c.domain,
  COUNT(a.id_analysis) AS total_analyses,
  MAX(a.analysis_date) AS latest_analysis
FROM companies c
LEFT JOIN analyses a ON a.id_company = c.id_company
GROUP BY c.id_company, c.name, c.domain
ORDER BY c.name;

-- 2. Show all analyses of a specific company.
SET @company_id = 1;
SELECT
  a.id_analysis,
  a.analysis_date,
  a.risk_score,
  a.risk_level,
  a.status
FROM analyses a
WHERE a.id_company = @company_id
ORDER BY a.analysis_date DESC;

-- 3. Show indicators of a specific analysis.
SET @analysis_id = 1;
SELECT
  i.type,
  i.indicator_value,
  i.severity,
  i.description
FROM indicators i
WHERE i.id_analysis = @analysis_id
ORDER BY i.severity DESC, i.type;

-- 4. Show recommendations of a specific analysis.
SELECT
  r.priority,
  r.description,
  r.action
FROM recommendations r
WHERE r.id_analysis = @analysis_id
ORDER BY r.priority DESC, r.id_recommendation;

-- 5. Calculate average risk score by company.
SELECT
  c.name,
  ROUND(AVG(a.risk_score), 2) AS average_risk_score
FROM companies c
JOIN analyses a ON a.id_company = c.id_company
GROUP BY c.id_company, c.name
ORDER BY average_risk_score DESC;

-- 6. Count analyses by risk level.
SELECT
  risk_level,
  COUNT(*) AS total
FROM analyses
GROUP BY risk_level
ORDER BY FIELD(risk_level, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW');

-- 7. Find companies with critical risk.
SELECT DISTINCT
  c.id_company,
  c.name,
  c.domain,
  a.risk_score,
  a.analysis_date
FROM companies c
JOIN analyses a ON a.id_company = c.id_company
WHERE a.risk_level = 'CRITICAL'
ORDER BY a.analysis_date DESC;

-- 8. Show latest OSINT findings if passive scans have been executed.
SELECT
  at.domain,
  sr.started_at,
  f.category,
  f.severity,
  COALESCE(f.status, 'OPEN') AS status,
  f.title
FROM finding f
JOIN scan_run sr ON sr.id = f.scan_run_id
JOIN audit_target at ON at.id = sr.target_id
ORDER BY sr.started_at DESC, f.severity DESC;
