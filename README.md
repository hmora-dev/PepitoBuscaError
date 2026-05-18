# PepitoBuscaError

PepitoBuscaError is a defensive web security audit platform for small companies. It stores authorized assets, runs first-pass checks, calculates a risk score, and keeps findings with evidence and remediation guidance.

Current checks:

- DNS health: A/AAAA, NS, CAA, DNSSEC delegation, stale CNAME targets.
- Mail security: MX, SPF, DMARC, MTA-STS, SMTP TLS reporting.
- Web security: HTTPS usage, availability, HTTP status, security headers, cookie flags, technology disclosure.
- Data exposure signals: server/framework disclosure and risky cookie configuration.

## Run locally

Requirements:

- Java 17
- MySQL Server
- MySQL Workbench

Open `pepito-busca-error/docs/mysql-workbench-setup.sql` in MySQL Workbench and execute it. It creates:

- Database: `pepito_busca_error`
- User: `pepito_app`
- Default password: `change_this_password`
- Tables: `audit_target`, `scan_run`, `finding`

Then run the application:

```bash
cd pepito-busca-error
./mvnw spring-boot:run
```

On Windows PowerShell or CMD:

```bat
cd pepito-busca-error
mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8080
```

For a real password, change the MySQL password and launch the app with:

```bash
DB_PASSWORD=your_password ./mvnw spring-boot:run
```
