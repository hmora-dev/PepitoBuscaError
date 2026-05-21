# Testing

The test suite uses JUnit, Spring Boot Test, MockMvc, and H2.

Run all tests from Windows PowerShell:

```powershell
cd C:\Users\hecto\Desktop\pepito-busca-error\pepito-busca-error
.\mvnw.cmd test
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
4. Register a company.
5. Create an analysis.
6. Review the result action plan.
7. Open the company detail page and check risk history.
8. Open `/osint`.
9. Submit a domain without API keys and confirm demo data.
10. Submit an email without HIBP key and confirm demo data.
11. Open `/geolocation`.
12. Register a device and open its live tracking link.
13. Confirm the browser asks for location permission.
14. Deactivate the device and confirm live updates are rejected.

## Future Test Improvements

- More service tests for audit scan creation.
- Repository tests for historical queries.
- Controller tests for company and geolocation flows.
- UI smoke tests for key Thymeleaf pages.
