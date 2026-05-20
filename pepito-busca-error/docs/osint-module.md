# OSINT Intelligence Module

## Purpose

The OSINT Intelligence module enriches PepitoBuscaError with passive public intelligence for authorized defensive audits. It helps document what a company exposes through DNS, public subdomains, provider relationships, and corporate email breach exposure.

The module must only be used with domains and emails the user owns or is explicitly authorized to review.

## Routes Added

- `GET /osint`: OSINT landing page with domain, email, and saved scan forms.
- `POST /osint/domain`: analyzes a domain with DNSDumpster-style and SecurityTrails-style intelligence.
- `POST /osint/email`: checks corporate email exposure with Have I Been Pwned-style results.
- `POST /osint/run`: existing saved passive scan flow.
- `GET /osint/scans/{id}`: existing saved scan detail view.

## Main Classes

- `OsintController`: handles routes and validation, then delegates work to services.
- `OsintService`: coordinates clients, builds recommendations, and converts domain intelligence into high-level findings.
- `DnsDumpsterClient`: produces DNSDumpster-style passive DNS and demo subdomain intelligence.
- `SecurityTrailsClient`: calls SecurityTrails when configured, otherwise returns demo data.
- `HaveIBeenPwnedClient`: calls Have I Been Pwned when configured, otherwise returns demo data.
- `OsintProviderCheck`: integrates domain OSINT provider results into saved `ScanRun` reports.

DTO/result classes:

- `OsintDomainForm`
- `OsintEmailForm`
- `OsintDomainResult`
- `DnsDumpsterResult`
- `SecurityTrailsResult`
- `SecurityTrailsRecord`
- `HibpResult`
- `BreachResult`
- `SubdomainResult`

## API Key Configuration

Configure optional API keys in `application.properties` or as environment variables:

```properties
osint.securitytrails.api-key=${SECURITYTRAILS_API_KEY:}
osint.hibp.api-key=${HIBP_API_KEY:}
osint.demo-mode=true
```

For real provider mode:

1. Set `SECURITYTRAILS_API_KEY`.
2. Set `HIBP_API_KEY`.
3. Change `osint.demo-mode=false`.
4. Restart the application.

No API key is hardcoded in the source code.

## Demo Mode

Demo mode keeps the project usable without paid or external API credentials.

When an API key is missing, the UI shows:

- `SecurityTrails API key not configured. Showing demo data.`
- `HIBP API key not configured. Showing demo data.`

The demo data is realistic enough for a DAM 1 final project presentation, but it is clearly marked as demo data. The application must not crash because of missing OSINT API keys.

## Security and Privacy Limits

- The module is passive and defensive.
- It does not brute force subdomains.
- It does not scan ports or address ranges.
- It does not exploit vulnerabilities.
- It does not request, store, or display passwords.
- HIBP results are shown as high-level breach names, dates, and data classes only.
- Saved scan integration stores high-level `Finding` records, not raw sensitive breach data.

## Database Integration

The module reuses the existing audit model:

- `AuditTarget`
- `ScanRun`
- `Finding`
- `FindingCategory`
- `FindingSeverity`

No new tables were required. Domain provider intelligence can appear in saved scan reports as `DNS` and `OSINT` findings.

## Future Improvements

- Store optional OSINT report snapshots with retention controls.
- Add a simple PDF export for OSINT reports.
- Add per-provider enable/disable settings.
- Add DKIM selector documentation without brute forcing selectors.
- Add clearer audit evidence notes for real SecurityTrails API responses.
