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

## Relationships

- One company can have many analyses.
- One analysis belongs to one company.
- One analysis can have many indicators.
- One indicator belongs to one analysis.
- One analysis can have many recommendations.
- One recommendation belongs to one analysis.

The foreign keys use `ON DELETE CASCADE` so deleting a company removes its analyses, indicators, and recommendations.
