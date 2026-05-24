# Public GPS Link Helper

Use this helper when you want the app to create a public GPS link without manually copying tunnel URLs into environment variables.

The app cannot bypass browser permission. The client phone opens the public link, the browser asks for location permission, and the location is sent automatically after the user taps `Allow`.

## Easiest Mode

Open PowerShell in the project folder:

```powershell
cd C:\Users\hecto\Desktop\pepito-busca-error\pepito-busca-error
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\easy-public-gps-link.ps1
```

What it does:

1. Looks for `cloudflared`.
2. If `cloudflared` is missing, looks for `ngrok`.
3. Reads `SERVER_PORT`, or uses `8080` if it is not set.
4. Starts a public HTTPS tunnel to the same local port as Spring Boot.
5. Reads the generated public HTTPS URL.
6. Sets `APP_PUBLIC_BASE_URL`.
7. Starts Spring Boot.

For example, to use port `8081`:

```powershell
$env:SERVER_PORT="8081"
.\scripts\easy-public-gps-link.ps1
```

The tunnel will expose:

```text
http://localhost:8081
```

Keep that PowerShell window open while testing.

If the script says no supported tunnel tool was found, install Cloudflare Tunnel or ngrok once, close and reopen PowerShell, then run the same command again.

The script does not install tools, download binaries, or create tunnel accounts.

## Port Already In Use

If port `8080` is busy, use another port:

```powershell
.\scripts\run-on-port.ps1 -Port 8081
```

Or safely inspect and free the port:

```powershell
.\scripts\free-port.ps1 -Port 8080
```

## Cloudflare Tunnel Quick Mode

Start the Spring Boot app locally once:

```powershell
.\mvnw.cmd spring-boot:run
```

In another terminal, run:

```powershell
cloudflared tunnel --url http://localhost:8080
```

If the app is running on port `8081`, use:

```powershell
cloudflared tunnel --url http://localhost:8081
```

Copy the generated URL, for example:

```text
https://abc123.trycloudflare.com
```

Stop the Spring Boot app, then run:

```powershell
.\scripts\easy-public-gps-link.ps1
```

The easy script will detect the Cloudflare URL automatically.

## ngrok

Run:

```powershell
ngrok http 8080
```

If the app is running on port `8081`, use:

```powershell
ngrok http 8081
```

Copy the HTTPS forwarding URL, for example:

```text
https://abc123.ngrok-free.app
```

Start the app with the easy script:

```powershell
.\scripts\easy-public-gps-link.ps1
```

The easy script will detect the ngrok HTTPS URL automatically.

## Test With Another Phone

1. Open the PepitoBuscaError dashboard.
2. Go to a tracked device detail page.
3. Confirm the badge says `Public HTTPS ready`.
4. Copy the generated GPS client link.
5. Send it only to an authorized phone.
6. Open it from mobile data or another Wi-Fi network.
7. Allow browser location permission.
8. Keep the page open while live updates are expected.

If the page says `Local test link`, `Same Wi-Fi only`, or `HTTPS required`, the link is not ready for another network yet.
