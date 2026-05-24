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
server.port=${SERVER_PORT:8080}
server.address=${SERVER_ADDRESS:0.0.0.0}
server.forward-headers-strategy=${SERVER_FORWARD_HEADERS_STRATEGY:framework}
app.public-base-url=${APP_PUBLIC_BASE_URL:}
app.tunnel.provider=${APP_TUNNEL_PROVIDER:manual}
public.base-url=${PUBLIC_BASE_URL:}
```

Keep `SERVER_ADDRESS=0.0.0.0` when a local reverse proxy or tunnel needs to reach the Spring Boot app. A LAN address such as `192.168.x.x` cannot be opened from another Wi-Fi, another country, or a mobile network.

For links that must open from anywhere:

1. Deploy the app on a public HTTPS host, or put a public HTTPS reverse proxy/tunnel in front of the local app.
2. Set `APP_PUBLIC_BASE_URL` to that HTTPS origin, for example `https://tracking.example.com`.
3. Keep `SERVER_FORWARD_HEADERS_STRATEGY=framework` when running behind a reverse proxy so Spring can build links from forwarded public host/protocol headers.

`app.public-base-url` is the preferred property. `public.base-url` is supported as a compatibility alias.

If `APP_PUBLIC_BASE_URL` is empty but the dashboard itself is opened through a public HTTPS URL, the generated client link uses that public URL. If neither condition is true, the geolocation detail page still shows a link when possible, but marks it as `Local test link`, `Same Wi-Fi only`, or `HTTPS required`.

Browser GPS is usually blocked on plain public HTTP and plain `http://192.168...` LAN links. Use public HTTPS for reliable GPS tracking from another Wi-Fi network.

More detail is available in `docs/public-gps-link.md`.

## Local Web Server Port

The default local port is `8080`:

```properties
server.port=${SERVER_PORT:8080}
```

If Spring Boot prints `Web server failed to start. Port 8080 was already in use.`, another process is already listening on that port.

Run on another port:

```powershell
$env:SERVER_PORT="8081"
.\mvnw.cmd spring-boot:run
```

Or use the helper:

```powershell
.\scripts\run-on-port.ps1 -Port 8081
```

To inspect and optionally stop the process using a port:

```powershell
.\scripts\free-port.ps1 -Port 8080
```

## Local Environment Example

See the root `.env.example` file for suggested local variables. Do not commit real API keys or production credentials.
