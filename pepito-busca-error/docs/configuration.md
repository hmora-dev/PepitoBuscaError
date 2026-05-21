# Configuration

PepitoBuscaError uses `application.properties` with environment-variable overrides. This keeps local development simple without hardcoding real secrets.

## Database

Default local settings:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/pepito_busca_error?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:pepito_app}
spring.datasource.password=${DB_PASSWORD:change_this_password}
```

`change_this_password` is only a local development default. Use a stronger password in any shared or production-like environment.

## OSINT API Keys

```properties
osint.securitytrails.api-key=${SECURITYTRAILS_API_KEY:}
osint.hibp.api-key=${HIBP_API_KEY:}
osint.demo-mode=${OSINT_DEMO_MODE:true}
```

When `OSINT_DEMO_MODE=true`, provider clients return safe demo data. When it is `false`, SecurityTrails and HIBP still fall back to demo data if their API key is missing.

## Public Tracking URL

```properties
server.address=${SERVER_ADDRESS:0.0.0.0}
app.public-base-url=${APP_PUBLIC_BASE_URL:}
```

Keep `SERVER_ADDRESS=0.0.0.0` so same-Wi-Fi tracking links can be reached from another device. Set `APP_PUBLIC_BASE_URL` to an HTTPS URL when a phone or another device must open the geolocation tracking link from outside the local network. Browser GPS is usually blocked on plain `http://192.168...` LAN links.

## Local Environment Example

See the root `.env.example` file for suggested local variables. Do not commit real API keys or production credentials.
