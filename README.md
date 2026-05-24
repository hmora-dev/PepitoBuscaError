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
- Chart.js from CDN for data-backed dashboard charts
- Leaflet and OpenStreetMap tiles for geolocation maps

## Current Features

- Responsive SaaS-style dashboard.
- Data-backed dashboard charts for risk distribution, indicator severity, OSINT finding categories, and recent risk score trend.
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
- Public HTTPS private tracking links that only send updates while the browser page is open and permission is granted.
- Latest authorized geolocation update stores the request IP address and browser user agent.
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

## Visual design and dashboard

The UI was redesigned as a professional SaaS dashboard for cybersecurity risk analysis. It uses Thymeleaf templates and custom CSS, without adding a complex frontend framework.

The design includes KPI cards, risk score visuals, Chart.js charts, recent activity timelines, OSINT exposure panels, clean tables, premium forms, and responsive layouts. The goal is to help companies understand cybersecurity information quickly: which assets have risk, which findings are critical, and which recommendations should be prioritized.

Dashboard charts use backend data from repositories through `DashboardService`:

- Risk distribution: company analyses grouped by `RiskLevel`.
- Indicator severity: stored analysis indicators grouped by `Severity`.
- OSINT finding categories: saved OSINT findings grouped by `FindingCategory`.
- Recent risk trend: latest company analysis scores ordered by date.

When there is no data yet, the dashboard shows empty states instead of fake chart values.

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

To run on another port:

```powershell
$env:SERVER_PORT="8081"
.\mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8081
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
- The generated client link is classified as public HTTPS, local-only, same-Wi-Fi only, or HTTPS-required.
- Use `APP_PUBLIC_BASE_URL` for a reliable public HTTPS link that works from another Wi-Fi or mobile network.
- If `APP_PUBLIC_BASE_URL` is empty, the application can derive a public HTTPS link when the dashboard is opened through an HTTPS tunnel or reverse proxy.
- There is no hidden background tracking.
- Inactive devices reject live updates.

This module is intended only for owned or explicitly authorized devices. More detail is available in `pepito-busca-error/docs/geolocation-privacy.md` and `pepito-busca-error/docs/public-gps-link.md`.

For Windows PowerShell with a public HTTPS tunnel:

```powershell
$env:APP_PUBLIC_BASE_URL="https://abc123.ngrok-free.app"
.\mvnw.cmd spring-boot:run
```

For CMD:

```bat
set APP_PUBLIC_BASE_URL=https://abc123.ngrok-free.app
mvnw.cmd spring-boot:run
```

## Public GPS Link from another Wi-Fi network

A `localhost` GPS link only works on the same computer that is running Spring Boot. A `192.168.x.x` LAN link usually works only for devices connected to the same Wi-Fi. A phone on another Wi-Fi network or on mobile data needs a public HTTPS URL that forwards traffic to the same local port as the running app, usually `http://localhost:8080`.

Browser geolocation still requires consent: the phone must open the private tracking link and allow the browser location prompt. After permission is granted, the live page sends the location automatically while it remains open. For reliable mobile geolocation, use HTTPS. If `APP_PUBLIC_BASE_URL` is empty, the app can derive a public HTTPS base URL when the dashboard itself is opened through a tunnel or reverse proxy.

The other device does not install anything. It only opens the link in its browser. If you also do not want to install a tunnel tool on the development PC, the app must already be running on a public HTTPS server or behind an existing public HTTPS reverse proxy.

Easiest Windows mode:

```powershell
cd C:\Users\hecto\Desktop\pepito-busca-error
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\easy-public-gps-link.ps1
```

This helper forwards to the Spring Boot project, builds the runnable jar, looks for `cloudflared` or `ngrok`, starts a public HTTPS tunnel, sets `APP_PUBLIC_BASE_URL`, and starts Spring Boot. Then open the device detail page, copy the generated GPS link, and send it to the authorized phone.

If you run the app on another port, set `SERVER_PORT` before the helper:

```powershell
$env:SERVER_PORT="8081"
.\scripts\easy-public-gps-link.ps1
```

The tunnel will expose `http://localhost:8081`.

Cloudflare Tunnel quick mode:

```powershell
cloudflared tunnel --url http://localhost:8080
```

Then restart the app with the generated HTTPS URL:

```powershell
$env:APP_PUBLIC_BASE_URL="https://abc123.trycloudflare.com"
.\mvnw.cmd spring-boot:run
```

ngrok:

```powershell
ngrok http 8080
$env:APP_PUBLIC_BASE_URL="https://abc123.ngrok-free.app"
.\mvnw.cmd spring-boot:run
```

Windows helper:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\start-with-public-url.ps1
```

More detail is available in `pepito-busca-error/docs/public-gps-link.md` and `pepito-busca-error/scripts/README-public-link.md`.

## Troubleshooting

### Port 8080 already in use

This means another process is already using the local web port that Spring Boot wants to use. The app supports changing the port with `SERVER_PORT`.

Option A: run on another port:

```powershell
$env:SERVER_PORT="8081"
.\mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8081
```

Option B: use the helper script:

```powershell
.\scripts\run-on-port.ps1 -Port 8081
```

Option C: safely free port 8080:

```powershell
.\scripts\free-port.ps1
```

The script shows which process owns the port and asks before stopping it.

Option D: manual Windows commands:

```bat
netstat -ano | findstr :8080
taskkill /PID PID_NUMBER /F
```

If you use the public GPS tunnel, expose the same port as the running app. For example, if `SERVER_PORT=8081`, use:

```powershell
cloudflared tunnel --url http://localhost:8081
ngrok http 8081
```

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
