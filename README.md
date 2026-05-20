# PepitoBuscaError

PepitoBuscaError is a Spring Boot MVC web application for registering companies and performing basic digital risk analysis. It is designed as a polished DAM 1 final project: professional enough to present as a small SaaS platform, but simple enough to understand and defend.

The application now includes a modern responsive dashboard inspired by professional SaaS cybersecurity tools. The interface uses Thymeleaf templates, reusable fragments, and custom CSS to keep the frontend understandable without adding React, JWT, Docker, or other advanced architecture.

## Objective

The objective is to help small and medium-sized businesses understand basic digital risk indicators. The application stores companies, creates analyses, calculates a risk score, assigns a risk level, and generates practical recommendations.

## Main User

The main user is a technical consultant, IT student, or small-business support technician who wants to register a company and create a first digital risk report.

## Technologies Used

- Java 17
- Spring Boot 3
- Maven
- Spring MVC
- Thymeleaf
- Spring Data JPA
- Hibernate
- MySQL 8
- HTML5 and CSS3
- Thymeleaf fragments for the reusable layout
- Custom responsive CSS for the SaaS dashboard interface

## Features

- Professional responsive home page and dashboard.
- Left sidebar navigation on desktop and a compact top navigation on mobile.
- Dashboard with total companies, total analyses, average risk, critical analyses, critical indicators, recent companies, recent analyses, and risk distribution.
- Separate pages for dashboard, companies, analyses, indicators, recommendations, OSINT, documentation, and geolocation.
- Full company CRUD.
- Company search by name, domain, or sector.
- Company detail page with profile data, latest risk summary, and analysis history.
- Analysis creation with selectable risk indicators.
- Automatic risk score calculation.
- Automatic risk level assignment.
- Automatic recommendation generation.
- Indicators grouped by severity with clear risk badges.
- Recommendations grouped by priority with clear priority badges.
- Real geolocation module for owned devices using the browser Geolocation API.
- Private tracking links that automatically send live browser-reported location to Spring Boot while the page is open and permission is granted.
- Professional live map view using Leaflet and OpenStreetMap tiles.
- OSINT Intelligence area for authorized passive public-footprint reports.
- DNSDumpster-style domain intelligence with safe demo subdomain data.
- SecurityTrails-style DNS intelligence with optional API key support.
- Have I Been Pwned-style corporate email exposure checks with optional API key support.
- Passive OSINT checks for DNS, mail, web/CDN metadata, TLS certificates, robots.txt, sitemap.xml, and security.txt.
- User-friendly error page.
- Responsive SaaS-style interface with cards, tables, dark metric panels, thin borders, and soft shadows.

## Real Geolocation

The geolocation module works with real browser location permission:

1. Register a device from `Geolocation > New device`.
2. Open the device detail page.
3. Open the private live tracking link on that same device.
4. Accept the browser location permission.
5. The browser sends latitude, longitude, accuracy, and a readable location label when available.
6. Spring Boot saves the last known position in MySQL.
7. The live tracker moves the marker on the map, and the device detail page refreshes the saved position automatically.

This is real geolocation, but it is still a web application. Tracking only works while the tracking page is open and the browser grants permission. Background tracking when the browser is closed would require a native mobile app or a more advanced PWA approach.

The map is rendered with Leaflet and OpenStreetMap tiles, so the map view needs internet access to load tile images.

## OSINT Intelligence Module

The OSINT module is designed for domains and corporate emails the user owns or is authorized to review. It is passive and defensive: it does not brute force subdomains, scan address ranges, exploit services, bypass authentication, or look up passwords.

The section is available at:

```text
http://localhost:8080/osint
```

It includes three provider-style checks:

- DNSDumpster-style analysis: summarizes public DNS records and shows realistic passive/demo subdomain intelligence such as `mail`, `vpn`, `dev`, `admin`, and `portal`.
- SecurityTrails-style analysis: shows current DNS records, subdomains, associated IPs, nameservers, mail servers, historical DNS notes, and risk interpretation.
- Have I Been Pwned-style analysis: checks whether a corporate email appears in known breach exposure data and shows only high-level breach names, dates, and data classes.

SecurityTrails and Have I Been Pwned require API keys for real provider calls. Configure them through environment variables or `application.properties`:

```properties
osint.securitytrails.api-key=${SECURITYTRAILS_API_KEY:}
osint.hibp.api-key=${HIBP_API_KEY:}
osint.demo-mode=true
```

When `osint.demo-mode=true` or an API key is missing, the application returns safe demo data and shows a warning in the UI. This keeps the project functional during academic presentations without paid API keys.

The existing saved OSINT report flow still works through `OSINT > Run OSINT report`. Provider-style domain intelligence is also converted into high-level `Finding` records for scan reports, without storing raw sensitive breach data.

## Visual Design

The visual design is inspired by premium cybersecurity SaaS dashboards. It uses a light workspace, a dark left sidebar, dark metric panels for important risk numbers, clean tables for company management, and color-coded badges for risk levels, severities, and priorities.

The main sections are separated into their own pages instead of being placed inside a single dashboard page. This makes the application easier to navigate and easier to explain in an oral defense.

The UI is built with Thymeleaf templates and custom CSS in:

```text
pepito-busca-error/src/main/resources/templates
pepito-busca-error/src/main/resources/static/css/style.css
```

This keeps the frontend simple enough for a DAM 1 project while still looking like a professional web application.

## Database Model

The main tables are:

- `companies`
- `analyses`
- `indicators`
- `recommendations`
- `tracked_devices`

Relationships:

- One company has many analyses.
- One analysis belongs to one company.
- One analysis has many indicators.
- One analysis has many recommendations.
- One tracked device stores owner, status, private tracking token, last known latitude, longitude, accuracy, location label, and update date.

More details are available in `pepito-busca-error/docs/database-model.md`.

## MVC Architecture

- Controllers manage routes and form validation.
- Services contain business logic, CRUD operations, risk calculation, and recommendation generation.
- Repositories use Spring Data JPA to access MySQL.
- Thymeleaf templates render the user interface.

## MySQL Setup

Requirements:

- Java 17 or newer
- MySQL Server 8
- MySQL Workbench

Open and execute:

```text
pepito-busca-error/docs/mysql-workbench-setup.sql
```

The script creates:

- database: `pepito_busca_error`
- user: `pepito_app`
- default password: `change_this_password`
- core tables for companies, analyses, indicators, and recommendations

For sample data, execute:

```text
pepito-busca-error/docs/dump.sql
```

## Run the Application

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

## How to Use

1. Open the home page.
2. Register a company from `Companies > New company`.
3. Open the company detail page.
4. Click `New analysis`.
5. Select the detected indicators.
6. Submit the form to calculate risk.
7. Review the result page with indicators and recommendations.
8. Use the separate analyses, indicators, and recommendations pages for detailed review.
9. Use the geolocation page to register your own devices and store their last browser-reported location.

## AI Usage Statement

AI was used as a development assistant to improve code structure, UI polish, SQL scripts, and documentation. The final application runs locally with Spring Boot MVC and MySQL. More details are available in `pepito-busca-error/docs/ai-usage.md`.

## Future Improvements

- Add user login with simple session-based security.
- Add PDF export for analysis results.
- Add more indicator types.
- Add charts for risk evolution.
- Add pagination for companies and analyses.
- Add unit tests for the risk calculator and services.

## Author

Project: PepitoBuscaError  
Academic context: DAM 1 final project  
Stack: Java, Spring Boot, Thymeleaf, MySQL
