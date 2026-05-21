# Database Model

PepitoBuscaError uses a simple relational model designed for a Spring Boot MVC application and a DAM 1 final project.

## Tables

### companies

Stores the businesses registered in the platform.

- `id_company`: primary key.
- `name`: company name.
- `domain`: public domain analyzed by the platform.
- `corporate_email`: contact or technical email.
- `sector`: business sector.
- `registration_date`: date when the company was created in the system.

### analyses

Stores each risk evaluation performed for a company.

- `id_analysis`: primary key.
- `id_company`: foreign key to `companies`.
- `analysis_date`: date of the analysis.
- `risk_score`: calculated score from 0 to 100.
- `risk_level`: `LOW`, `MEDIUM`, `HIGH`, or `CRITICAL`.
- `status`: simple execution status, currently `COMPLETED`.

### indicators

Stores the detected risk indicators for an analysis.

- `id_indicator`: primary key.
- `id_analysis`: foreign key to `analyses`.
- `type`: indicator name.
- `indicator_value`: short evidence value.
- `description`: explanation of the detected issue.
- `severity`: `LOW`, `MEDIUM`, `HIGH`, or `CRITICAL`.

### recommendations

Stores the recommended remediation actions generated for an analysis.

- `id_recommendation`: primary key.
- `id_analysis`: foreign key to `analyses`.
- `priority`: `LOW`, `MEDIUM`, or `HIGH`.
- `description`: short recommendation.
- `action`: practical action to perform.

### tracked_devices

Stores owned devices used by the geolocation module.

- `id_device`: primary key.
- `name`: device name.
- `device_type`: phone, laptop, tablet, or other device type.
- `owner`: owner or responsible person.
- `latitude` and `longitude`: last browser-reported coordinates.
- `accuracy_meters`: browser-reported GPS/network accuracy.
- `location_label`: readable place label when available.
- `tracking_token`: private token used by the live tracking link.
- `active`: whether the tracking link can update the device.
- `registered_at` and `last_seen_at`: creation and latest update dates.

### audit_target

Stores domains or URLs analyzed by the OSINT module.

- `id`: primary key.
- `name`: readable asset name.
- `domain`: normalized public domain.
- `url`: normalized HTTP or HTTPS URL.
- `created_at`: first time the target was registered.

### scan_run

Stores each passive OSINT report execution.

- `id`: primary key.
- `target_id`: foreign key to `audit_target`.
- `started_at`: start date.
- `completed_at`: completion date.
- `status`: `RUNNING`, `COMPLETED`, or `FAILED`.
- `risk_score`: exposure score calculated from findings.

### finding

Stores OSINT and security findings generated during a scan run.

- `id`: primary key.
- `scan_run_id`: foreign key to `scan_run`.
- `category`: `OSINT`, `WEB`, `MAIL`, `DNS`, `DATA`, or `AVAILABILITY`.
- `severity`: `INFO`, `LOW`, `MEDIUM`, `HIGH`, or `CRITICAL`.
- `status`: `OPEN`, `IN_PROGRESS`, `ACCEPTED_RISK`, `RESOLVED`, or `FALSE_POSITIVE`.
- `title`: short finding title.
- `evidence`: observed public signal.
- `recommendation`: suggested remediation.

## Relationships

- One company can have many analyses.
- One analysis belongs to one company.
- One analysis can have many indicators.
- One indicator belongs to one analysis.
- One analysis can have many recommendations.
- One recommendation belongs to one analysis.
- One audit target can have many scan runs.
- One scan run belongs to one audit target.
- One scan run can have many findings.
- One finding belongs to one scan run.

The foreign keys use `ON DELETE CASCADE` so deleting a company removes its analyses, indicators, and recommendations.
