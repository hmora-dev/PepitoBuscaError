# Technical Decisions

## Java 17

Java 17 was selected because it is a stable long-term support version and works well with Spring Boot 3. It is modern enough for clean code while remaining suitable for a DAM 1 project.

## Spring Boot 3

Spring Boot 3 simplifies the creation of MVC applications by providing embedded Tomcat, dependency management, validation, JPA integration, and a clear project structure.

## Maven

Maven was used because it is standard in many Java projects. It makes dependencies, compilation, testing, and application startup easy to reproduce.

## MySQL 8

MySQL 8 was selected because it is widely used in business environments and is appropriate for a project that stores companies, analyses, indicators, and recommendations.

## JPA and Hibernate

JPA and Hibernate reduce repetitive SQL for basic CRUD operations and map the Java domain model to relational tables. This keeps the project understandable while still using professional persistence practices.

## Thymeleaf Instead of React

Thymeleaf was chosen because the application is a server-rendered Spring MVC project. It avoids unnecessary frontend complexity, extra build tools, API layers, and advanced authentication requirements.

## Why This Visual Design Was Chosen

The sidebar layout was chosen because it improves navigation in a dashboard-style application. It keeps the main sections visible on desktop and changes to a compact top navigation on smaller screens.

The main sections were separated into different pages: dashboard, companies, analyses, indicators, recommendations, documentation, and geolocation. This avoids placing too much information on one page and makes each workflow easier to understand.

Dashboard cards were chosen because they make key information easy to understand quickly: total companies, total analyses, average risk, and critical risks.

Risk badges were added because they help identify priorities quickly. Different colors for low, medium, high, and critical levels make the result easier to scan in tables and reports.

The responsive design was chosen to improve usability on desktop, tablet, and mobile. Cards stack vertically, tables can scroll horizontally, and action buttons remain easy to tap.

Thymeleaf was kept as the rendering technology because it aligns with Spring Boot MVC and keeps the project simple. Reusable fragments for the layout, sidebar, navbar, and footer avoid repeated HTML without introducing a complex frontend framework.

## Real Geolocation Module

The geolocation module was added as a real engineering feature for owned devices. It stores a device name, type, owner, private tracking token, latitude, longitude, GPS accuracy, location label, status, and last update date.

The live tracker uses the browser Geolocation API. The tracked device opens its private tracking link, presses the start button, grants browser permission, and the browser sends latitude, longitude, and accuracy to a Spring MVC endpoint. The backend validates the coordinates and stores the latest position in MySQL.

Leaflet was added to display the live position on a real interactive map with OpenStreetMap tiles. The live tracker moves the marker and accuracy circle as the browser reports new positions. The device detail page also polls the backend and refreshes the saved position on the map.

This design was chosen because it works in real life without needing a native mobile application. It is also privacy-aware because the device owner must open the page and grant permission. A limitation is that browser geolocation only runs while the page is open; continuous background tracking would require a native mobile app or a more advanced PWA architecture.

## MVC Architecture

MVC separates responsibilities clearly:

- controllers handle routes and form validation,
- services contain business logic,
- repositories access the database,
- templates render the interface.

This structure is easy to defend in an oral presentation.

## Database Model

The database model was chosen around four main concepts: company, analysis, indicator, and recommendation. This matches the real workflow of registering a company, evaluating risk, storing detected problems, and proposing actions.

## RiskCalculator Interface

The `RiskCalculator` interface separates the risk calculation contract from its implementation. The current implementation is simple, but the design allows a future calculator to be added without changing controllers.

## Excluded Advanced Features

JWT, React, real cybersecurity APIs, Docker, microservices, GraphQL, and WebFlux were not included because they would add complexity that is not necessary for the DAM 1 objective. The current version focuses on a stable, explainable Spring Boot MVC application.
