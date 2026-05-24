# Geolocation Privacy

The geolocation module is designed for owned or explicitly authorized devices only.

## How It Works

1. A device is registered in the application.
2. PepitoBuscaError creates a private tracking token.
3. The user opens the live tracking link on that device.
4. The browser asks for location permission.
5. If permission is granted, the page sends latitude, longitude, accuracy, an optional location label, the request IP address, and the browser user agent to Spring Boot.
6. The application stores only the latest known position.

## Important Limits

- There is no hidden tracking.
- There is no background tracking after the browser tab is closed.
- The browser controls the permission prompt.
- Most mobile browsers require HTTPS for precise GPS.
- The safest sendable client link is public HTTPS. Use a public deployment, reverse proxy, or tunnel and set `APP_PUBLIC_BASE_URL`.
- Localhost links only work on the same computer, and LAN links usually only work on the same Wi-Fi.
- Public links should be treated as private tokens and should not be shared publicly.
- Inactive devices reject live position updates.
- The feature should not be used for people or devices without authorization.

## Current Safety Controls

- Private random tracking token per device.
- Active/inactive device flag.
- Coordinate validation on the backend.
- Client IP and browser user-agent storage only when the authorized live page sends a position update.
- Clear UI privacy text on the live tracking page.
- Notes explaining that closing the page or pressing Stop ends live updates.
- Public link status labels for public HTTPS, localhost, LAN, and missing HTTPS cases.

## Future Improvements

- Token expiration date.
- Token regeneration button.
- Session-protected tracking management.
- Organization-based access control.
- Location retention policy.
- Audit log for position updates.
