# Security Roadmap

PepitoBuscaError currently has no application login. This is intentional for the DAM 1 academic version so the MVC, JPA, Thymeleaf, OSINT, and geolocation flows remain easy to understand.

## Future Authentication Model

A production version should add session-based Spring Security:

- `User` entity with email, display name, enabled flag, and password hash.
- BCrypt password hashing through `PasswordEncoder`.
- Roles: `ADMIN`, `ANALYST`, and `VIEWER`.
- Login and logout pages rendered with Thymeleaf.
- Method or route access rules for administrative actions.
- Organization ownership on companies, analyses, audit targets, scan runs, findings, and tracked devices.

## Why JWT Was Not Added

JWT is useful for stateless APIs and distributed systems, but this project is a server-rendered Spring MVC application. Session-based authentication is simpler, safer for this scope, and easier to defend in an oral presentation.

## Privacy Requirements

Before production use, add:

- user ownership checks,
- audit logs for sensitive actions,
- CSRF protection through Spring Security,
- role-based access to geolocation links,
- and clear retention rules for OSINT and location data.
