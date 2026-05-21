# PepitoBuscaError

PepitoBuscaError is a Spring Boot MVC cybersecurity web application for small and medium-sized businesses. It helps register companies, create simple digital risk analyses, review passive OSINT findings, generate remediation recommendations, and manage consent-based browser geolocation for owned devices.

The project is built as a DAM 1 final project: professional enough to present as a small SaaS-style platform, but still simple enough to understand and defend orally.

## Objective

The objective is to give a technical consultant, IT student, or small-business support technician a clear first view of digital risk:

- which companies are registered,
- which indicators were detected,
- which recommendations should be prioritized,
- what public OSINT signals are visible,
- and which owned devices have voluntarily reported a browser location.

## Main User

The main user is an analyst or technician performing authorized defensive reviews for small organizations. The application is not designed for offensive testing, exploitation, hidden tracking, or credential collection.

## Technologies Used

- Java 17
- Spring Boot 3
- Maven
- Spring MVC
- Thymeleaf
- Spring Data JPA
- Hibernate
- MySQL 8
- H2 for tests
- HTML5 and CSS3
- Bootstrap Icons
- Leaflet and OpenStreetMap tiles for geolocation maps

## Current Features

- Responsive SaaS-style dashboard.
- Company CRUD with domain, corporate email, and sector.
- Manual risk analysis creation using selected indicators.
- Automatic risk score and risk level calculation.
- Recommendation generation grouped into an action plan.
- Company detail page with latest score, average score, previous score, and risk trend.
- Dashboard risk-notification section for latest high and critical analyses.
- Indicator and recommendation list pages.
- Passive OSINT scan flow with audit targets, scan runs, findings, severity counts, and categories.
- DNSDumpster-style domain intelligence with safe demo/passive subdomain data.
- SecurityTrails-style DNS intelligence with optional API key support.
- Have I Been Pwned-style corporate email exposure checks with optional API key support.
- Finding lifecycle status field with default `OPEN`.
- Consent-based geolocation module for owned or authorized devices.
- Private tracking links that only send updates while the browser page is open and permission is granted.
- Friendly error page for not-found and unexpected errors.
- Documentation section and supporting files under `pepito-busca-error/docs`.

## Database Model

Main business tables:

- `companies`
- `analyses`
- `indicators`
- `recommendations`

Passive OSINT/audit tables:

- `audit_target`
- `scan_run`
- `finding`

Geolocation tables:

- `tracked_devices`

Main relationships:

- One company has many analyses.
- One analysis has many indicators.
- One analysis has many recommendations.
- One audit target has many scan runs.
- One scan run has many findings.
- One tracked device stores its private token, active status, last known coordinates, accuracy, location label, and timestamps.

More detail is available in `pepito-busca-error/docs/database-model.md`.

## Architecture

The project follows a simple MVC structure:

- Controllers handle routes and form validation.
- Services contain business logic.
- Repositories use Spring Data JPA.
- Entities model database tables.
- DTO/result classes carry form and OSINT response data.
- Thymeleaf templates render the UI.

The project intentionally avoids React, JWT, Docker, microservices, GraphQL, and WebFlux to keep the architecture appropriate for the academic scope.

## MySQL Configuration

Default local configuration is in:

```text
pepito-busca-error/src/main/resources/application.properties
```

The application supports environment variables:

```properties
DB_URL=jdbc:mysql://localhost:3306/pepito_busca_error?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true
DB_USERNAME=pepito_app
DB_PASSWORD=change_this_password
```

The default password is only for local development. Use a different password outside a classroom/local environment.

You can use `.env.example` as a reference for local variables.

To prepare MySQL, run:

```text
pepito-busca-error/docs/mysql-workbench-setup.sql
```

## Run The Application

From Windows PowerShell:

```powershell
cd C:\Users\hecto\Desktop\pepito-busca-error\pepito-busca-error
$env:DB_PASSWORD="change_this_password"
.\mvnw.cmd spring-boot:run
```

From CMD:

```bat
cd C:\Users\hecto\Desktop\pepito-busca-error\pepito-busca-error
set DB_PASSWORD=change_this_password&&mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8080
```

Run tests:

```powershell
cd C:\Users\hecto\Desktop\pepito-busca-error\pepito-busca-error
.\mvnw.cmd test
```

## OSINT Intelligence Module

The OSINT module is passive and defensive. It must only be used with domains and corporate emails that the user owns or is authorized to review.

Routes:

- `GET /osint`
- `POST /osint/domain`
- `POST /osint/email`
- `POST /osint/run`
- `GET /osint/scans/{id}`

Configuration:

```properties
osint.securitytrails.api-key=${SECURITYTRAILS_API_KEY:}
osint.hibp.api-key=${HIBP_API_KEY:}
osint.demo-mode=${OSINT_DEMO_MODE:true}
```

When demo mode is enabled or an API key is missing, the application shows safe demo data and a clear warning. No API key is hardcoded.

The HIBP-style module displays only high-level exposure information. It does not ask for passwords and does not store raw sensitive breach data.

## Geolocation Module

The geolocation feature is consent-based:

- The device owner opens a private tracking link.
- The browser asks for location permission.
- Updates are sent only while the live tracking page remains open.
- There is no hidden background tracking.
- Inactive devices reject live updates.

This module is intended only for owned or explicitly authorized devices. More detail is available in `pepito-busca-error/docs/geolocation-privacy.md`.

## AI Usage Statement

AI was used as a development assistant for code review, UI polish, documentation, tests, and architecture cleanup. The final project remains a local Spring Boot MVC application that can be compiled and reviewed directly.

More detail is available in `pepito-busca-error/docs/ai-usage.md`.

## Current Limitations

- No real authentication or roles are implemented yet.
- No PDF export is implemented yet.
- No real alert delivery exists; the dashboard shows internal risk notifications only.
- No scheduled scans are implemented.
- OSINT real provider calls require valid external API keys.
- Browser geolocation works only while the live page is open and permission is granted.
- The project is not production hardened.

## Future Improvements

- Session-based Spring Security login.
- Roles: `ADMIN`, `ANALYST`, and `VIEWER`.
- Organization-based access control.
- Executive and technical PDF report generation.
- Real alerting with email or webhook notifications.
- Scheduled passive scans.
- More OSINT providers.
- Finding status update UI.
- More unit and integration tests.
- Production deployment hardening.

See `pepito-busca-error/docs/future-improvements.md`.

## Author

Project: PepitoBuscaError  
Academic context: DAM 1 final project  
Stack: Java, Spring Boot, Thymeleaf, MySQL
