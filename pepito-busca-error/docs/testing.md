# Testing

The test suite uses JUnit, Spring Boot Test, MockMvc, and H2.

## Running tests

Run all tests from Windows PowerShell:

```powershell
cd C:\Users\hecto\Desktop\pepito-busca-error\pepito-busca-error
.\mvnw.cmd test
```

The basic `PepitoBuscaErrorApplicationTests` class verifies that the Spring Boot application context can start with the test configuration. Controller tests use MockMvc, and persistence tests use H2 instead of the local MySQL database.

The Maven test configuration loads Mockito as an explicit Java agent during Surefire test runs. This keeps the test output clean on current Java versions and avoids Mockito/Byte Buddy dynamic self-attach warnings on Java 21 and later. Class data sharing is disabled only for the forked test JVM to avoid harmless agent-related JVM noise; application startup is not affected.

## Local Port

The application uses port `8080` by default. If another Spring Boot process is already running, start this project on another port:

```powershell
$env:SERVER_PORT="8081"
.\mvnw.cmd spring-boot:run
```

Or use:

```powershell
.\scripts\run-on-port.ps1 -Port 8081
```

To free a busy port safely:

```powershell
.\scripts\free-port.ps1 -Port 8080
```

## Current Coverage

- Application context startup.
- OSINT controller pages, demo mode, and validation.
- Core page smoke tests for dashboard, companies, analysis result, and geolocation templates.
- Target normalization.
- HTTP probe client behavior.
- Basic risk calculator.
- Company historical risk helpers.

## Manual Smoke Test

1. Start the application.
2. Open `/`.
3. Open `/dashboard`.
4. Confirm empty states are shown if there are no analyses or OSINT reports.
5. Register a company.
6. Create at least two analyses.
7. Confirm the dashboard charts render risk distribution, indicator severity, and recent risk trend from those records.
8. Review the result action plan.
9. Open the company detail page and check risk history.
10. Open `/osint`.
11. Submit a domain without API keys and confirm demo data.
12. Run a saved OSINT report and confirm the dashboard category chart uses the stored findings.
13. Submit an email without HIBP key and confirm demo data.
14. Open `/geolocation`.
15. Register a device and open its live tracking link.
16. Confirm the browser asks for location permission.
17. Deactivate the device and confirm live updates are rejected.
18. Open a missing URL and confirm the friendly 404 page appears.

## Future Test Improvements

- More service tests for audit scan creation.
- Repository tests for historical queries.
- Controller tests for company and geolocation flows.
- UI smoke tests for key Thymeleaf pages.
