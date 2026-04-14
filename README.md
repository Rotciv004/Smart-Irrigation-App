# SmartIrrigation

Local Wi-Fi Android app for controlling an ESP32 irrigation device over HTTP on the same LAN.

## Features
- Save local ESP32 IPv4 address and port
- Test local connectivity with `GET /status`
- View humidity, target humidity, mode, and pump state
- Set target humidity
- Switch AUTO / MANUAL mode
- Start and stop the pump manually
- Persist IP, port, theme mode, and polling interval with Preferences DataStore

## ESP32 API contract
Base URL:

```text
http://<saved_ip>:<saved_port>/
```

Endpoints:
- `GET /status`
- `POST /target-humidity`
- `POST /pump/start`
- `POST /pump/stop`
- `POST /mode`

## Notes
- No cloud backend
- No login or account system
- No SQL/Room database
- Local LAN HTTP only

## Typical commands
```powershell
.\gradlew.bat testDebugUnitTest --console=plain
.\gradlew.bat assembleDebug --console=plain
```

If Gradle cannot find Java on this machine, set `JAVA_HOME` to a JDK installation before running the commands.

