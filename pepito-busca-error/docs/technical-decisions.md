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
