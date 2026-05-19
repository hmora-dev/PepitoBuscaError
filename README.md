# PepitoBuscaError

PepitoBuscaError is a Spring Boot MVC web application for registering companies and performing basic digital risk analysis. It is designed as a polished DAM 1 final project: professional enough to present as a small SaaS platform, but simple enough to understand and defend.

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

## Features

- Professional home page.
- Dashboard with total companies, total analyses, average risk, critical analyses, recent companies, recent analyses, and risk distribution.
- Full company CRUD.
- Company search by name, domain, or sector.
- Company detail page with analysis history.
- Analysis creation with selectable risk indicators.
- Automatic risk score calculation.
- Automatic risk level assignment.
- Automatic recommendation generation.
- Indicators grouped by severity.
- Recommendations grouped by priority.
- User-friendly error page.
- Responsive SaaS-style interface.

## Database Model

The main tables are:

- `companies`
- `analyses`
- `indicators`
- `recommendations`

Relationships:

- One company has many analyses.
- One analysis belongs to one company.
- One analysis has many indicators.
- One analysis has many recommendations.

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
8. Use the dashboard to review global metrics.

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
