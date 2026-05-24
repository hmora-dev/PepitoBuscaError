# Public GPS Link from another Wi-Fi network

The GPS link is a private, tokenized browser link for an owned or explicitly authorized device. The device owner must open the link and allow the browser location prompt. The application does not bypass browser security, does not perform hidden tracking, and only receives location updates while the live page remains open.

Treat generated GPS links as private tokens. Do not publish them in chats, repositories, screenshots, or public documents.

`localhost` only works on the same computer. A LAN IP such as `192.168.x.x` usually only works on the same Wi-Fi. A phone on another Wi-Fi network or on mobile data needs a public HTTPS tunnel or reverse proxy.

Most mobile browsers require HTTPS for geolocation. Localhost is the main development exception.

## Without installing anything on the other device

The other device does not install anything. It only opens the generated HTTPS link in its browser and taps `Allow` when the browser asks for location.

If you also do not want to install a tunnel tool on the development PC, then the Spring Boot app must already be running on a public HTTPS server or behind an existing HTTPS reverse proxy. A private `localhost` app cannot be opened from another network by code alone, because the other phone has no route to your computer.

No-install options for the development PC:

1. Deploy the Spring Boot app to a public HTTPS server.
2. Use an existing reverse proxy/domain that already points to the app.
3. Set `APP_PUBLIC_BASE_URL` to that public HTTPS URL.
4. Restart the app and copy the generated GPS client link.

## Easiest way

Run one command from Windows PowerShell:

```powershell
cd C:\Users\hecto\Desktop\pepito-busca-error\pepito-busca-error
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\easy-public-gps-link.ps1
```

The helper tries to find `cloudflared` first and `ngrok` second. It starts the tunnel, captures the public HTTPS URL, sets `APP_PUBLIC_BASE_URL`, and starts Spring Boot.

If port `8080` is busy, run the helper on another port:

```powershell
$env:SERVER_PORT="8081"
.\scripts\easy-public-gps-link.ps1
```

The tunnel will expose `http://localhost:8081`.

Then:

1. Open `http://localhost:8080/geolocation`, or use the configured port such as `http://localhost:8081/geolocation`.
2. Create or open a tracked device.
3. Confirm the link badge says `Public HTTPS ready`.
4. Copy the GPS client link.
5. Send it only to the authorized phone.
6. The phone opens the link and taps `Allow` in the browser location prompt.

After permission is granted, the page sends location automatically while it remains open.

## How Links Are Generated

The geolocation detail page asks `PublicLinkService` for the client GPS link.

Priority:

1. `app.public-base-url`, usually set through `APP_PUBLIC_BASE_URL`.
2. `public.base-url`, optionally set through `PUBLIC_BASE_URL`.
3. The current request base URL, including forwarded HTTPS tunnel or reverse proxy headers.

The route stays:

```text
/geolocation/live/{token}
```

Examples:

```text
https://abc123.ngrok-free.app/geolocation/live/{token}
http://localhost:8080/geolocation/live/{token}
http://192.168.1.50:8080/geolocation/live/{token}
```

The UI classifies each generated link:

- `Public HTTPS ready`: usable from another Wi-Fi or mobile network.
- `Local test link`: only works on the same computer.
- `Same Wi-Fi only`: may work only for devices on the same LAN.
- `HTTPS required`: public host is configured, but browser GPS is unreliable because the URL uses HTTP.
- `Not properly configured`: the configured URL is invalid or no request URL can be derived.

## Windows examples

PowerShell:

```powershell
$env:APP_PUBLIC_BASE_URL="https://abc123.trycloudflare.com"
.\mvnw.cmd spring-boot:run
```

CMD:

```bat
set APP_PUBLIC_BASE_URL=https://abc123.trycloudflare.com
mvnw.cmd spring-boot:run
```

Run on another local port:

```powershell
$env:SERVER_PORT="8081"
.\mvnw.cmd spring-boot:run
```

## Cloudflare Tunnel quick mode

Step 1:
Run the Spring Boot application locally:

```powershell
.\mvnw.cmd spring-boot:run
```

Step 2:
Expose local port 8080:

```powershell
cloudflared tunnel --url http://localhost:8080
```

If the app is running on port `8081`, the tunnel must use the same port:

```powershell
cloudflared tunnel --url http://localhost:8081
```

Step 3:
Copy the generated HTTPS URL, for example:

```text
https://abc123.trycloudflare.com
```

Step 4:
Set the environment variable in Windows PowerShell:

```powershell
$env:APP_PUBLIC_BASE_URL="https://abc123.trycloudflare.com"
```

Step 5:
Run the application again:

```powershell
.\mvnw.cmd spring-boot:run
```

Step 6:
Open the dashboard and go to a tracked device detail page.

Step 7:
Confirm the badge says `Public HTTPS ready`, then send the generated public GPS link to the authorized device.

Step 8:
The phone opens the link, accepts browser location permission, and keeps the page open while live updates are expected.

## ngrok

Run:

```powershell
ngrok http 8080
```

If the app is running on port `8081`:

```powershell
ngrok http 8081
```

Copy the HTTPS URL, for example:

```text
https://abc123.ngrok-free.app
```

PowerShell:

```powershell
$env:APP_PUBLIC_BASE_URL="https://abc123.ngrok-free.app"
.\mvnw.cmd spring-boot:run
```

CMD:

```bat
set APP_PUBLIC_BASE_URL=https://abc123.ngrok-free.app
mvnw.cmd spring-boot:run
```

## Windows helper script

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\start-with-public-url.ps1
```

The script asks for the public HTTPS tunnel URL, sets `APP_PUBLIC_BASE_URL` for that PowerShell session, and runs:

```powershell
.\mvnw.cmd spring-boot:run
```

It does not install `cloudflared` or `ngrok`.

Any HTTPS reverse proxy can also work if it forwards traffic to the Spring Boot app and preserves the public scheme and host through standard `Forwarded` or `X-Forwarded-*` headers.

## Localhost And LAN Behavior

`http://localhost:8080` links are local test links. They only work on the computer running the application.

`http://192.168.x.x:8080` links are LAN links. They may work for devices connected to the same Wi-Fi network, but they usually do not work from mobile data, another home, another company, or another country.

Most mobile browsers require HTTPS for geolocation. For reliable GPS tracking from another network, use a public HTTPS URL and configure:

```properties
app.public-base-url=${APP_PUBLIC_BASE_URL:}
app.tunnel.provider=${APP_TUNNEL_PROVIDER:manual}
```

## Privacy And Security Rules

- Only use this feature with devices or users you are authorized to track.
- The device owner must open the link.
- The browser must request permission.
- The location is sent only after permission is granted.
- The page must remain open for continuous updates.
- The application does not bypass browser security.
- There is no hidden tracking.
- Public links should be treated as private tokens.
- Tokens should not be shared publicly.
- HTTPS is strongly recommended.

## Future Improvements

The current token system uses a private random tracking token and an active/inactive device flag. Future improvements could add token expiration, token regeneration, last access timestamp, and a separate last location update timestamp.
