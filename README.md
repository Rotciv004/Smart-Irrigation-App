# SmartIrrigation

> Local Wi-Fi Android application for monitoring and controlling an ESP32-based irrigation system directly over the same LAN.

SmartIrrigation is a clean Android app built with **Kotlin**, **Jetpack Compose**, **Material 3**, **MVVM**, **Repository**, and **StateFlow**. The application communicates **directly with an ESP32 over HTTP**, without any cloud backend, login flow, SQL database, or external API.

It is designed for a simple and realistic use case:

- the phone and ESP32 are connected to the **same Wi-Fi router**
- the Android app sends HTTP requests directly to the ESP32
- the ESP32 is the **only backend/server**

---

## Highlights

- Configure the ESP32 **IPv4 address** and **port**
- Test the device connection using `GET /status`
- View:
  - current soil humidity
  - target humidity
  - pump state
  - irrigation mode
  - optional device name
- Set target humidity from the app
- Switch between **AUTO** and **MANUAL** modes
- Start and stop the pump manually
- Persist local settings with **Preferences DataStore**
- Support **System / Light / Dark** theme modes
- Browse the app UI even when the device is not connected yet

---

## Demo / UX note

The app supports an **offline browsing flow**:

- you can open the app
- navigate through the main screens
- open settings
- save IP and port
- explore the dashboard layout

even if the ESP32 pump is not currently connected.

This is **not a fake demo mode** and does **not** use mocked final repository data. It simply allows navigation and clear disconnected states until the real ESP32 becomes reachable.

---

## Screens

### 1. Connect Screen
Used to configure the local ESP32 connection.

Features:
- input for IPv4 address only
- input for port
- inline validation for invalid IP / invalid port
- `Test connection` button
- `Save and continue` button
- visible success / error messages

### 2. Dashboard Screen
Main control screen of the irrigation system.

Sections:
- **Connection status**
- **Humidity status**
- **Auto watering / target humidity**
- **Pump control**
- **Device info**

### 3. Settings Screen
Local app preferences and connection utilities.

Features:
- theme selection
- saved IP and port display
- edit connection shortcut
- polling interval configuration
- clear saved connection
- about section

---

## Architecture

This project follows a **single-activity** Android architecture with:

- **Jetpack Compose** for UI
- **Compose Navigation** for screen navigation
- **MVVM** for presentation logic
- **Repository pattern** for data access
- **StateFlow** for reactive UI state
- **manual dependency wiring** through an `AppContainer`

### Internal structure

```text
com.example.smartirrigation
├── MainActivity.kt
├── app
├── core
│   ├── network
│   ├── theme
│   ├── ui
│   └── util
├── data
│   ├── model
│   ├── remote
│   ├── repository
│   └── settings
└── feature
	├── connect
	├── dashboard
	├── navigation
	└── settings
```

---

## Tech stack

### Android
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Lifecycle Compose
- ViewModel

### Data & state
- StateFlow
- Preferences DataStore

### Networking
- Retrofit
- OkHttp
- Kotlin Serialization

### Testing
- JUnit
- kotlinx-coroutines-test

---

## Communication model

This application uses a **strictly local LAN model**.

### What it does
- sends HTTP requests from the Android app directly to the ESP32
- works only when phone and ESP32 are on the same network
- uses polling to refresh device status

### What it does NOT do
- no cloud sync
- no Firebase
- no account system
- no login / registration
- no Room or SQL database
- no remote backend outside the ESP32
- no WebSocket
- no BLE

---

## ESP32 API contract

Base URL:

```text
http://<saved_ip>:<saved_port>/
```

Default port:

```text
80
```

Default polling interval:

```text
2500 ms
```

### Endpoints

#### `GET /status`

Response:

```json
{
  "humidity": 42,
  "targetHumidity": 55,
  "pumpOn": false,
  "mode": "auto",
  "deviceName": "ESP32 Pump 1"
}
```

#### `POST /target-humidity`

Request:

```json
{
  "targetHumidity": 55
}
```

#### `POST /pump/start`

Request:

```json
{}
```

#### `POST /pump/stop`

Request:

```json
{}
```

#### `POST /mode`

Request:

```json
{
  "mode": "auto"
}
```

or

```json
{
  "mode": "manual"
}
```

---

## Business rules

- `targetHumidity` must be between **0 and 100**
- humidity is treated as an integer percentage between **0 and 100**
- available irrigation modes:
  - `AUTO`
  - `MANUAL`
- humidity status is derived using these rules:
  - `DRY` if `humidity < targetHumidity - 3`
  - `GOOD` if `humidity` is between `targetHumidity - 3` and `targetHumidity + 3`
  - `WET` if `humidity > targetHumidity + 3`

---

## Local persistence

The app stores only local user/device preferences:

- `host`
- `port`
- `appThemeMode`
- `pollingIntervalMs`

Defaults:

```text
host = ""
port = 80
appThemeMode = SYSTEM
pollingIntervalMs = 2500
```

---

## Network configuration

Because the ESP32 communication is HTTP-based and local-only, the app is configured with:

- `android.permission.INTERNET`
- `android.permission.ACCESS_NETWORK_STATE`
- cleartext HTTP enabled through `network_security_config.xml`

---

## Build requirements

To build the project locally, you need:

- Android Studio / IntelliJ with Android support
- Android SDK
- JDK installed and configured

If Gradle cannot find Java, set `JAVA_HOME` before building.

Example:

```powershell
$env:JAVA_HOME="C:\Path\To\JDK"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

---

## Run instructions

### Build from terminal

```powershell
.\gradlew.bat assembleDebug --console=plain
```

### Run unit tests

```powershell
.\gradlew.bat testDebugUnitTest --console=plain
```

### Run from Android Studio

1. Open the project in Android Studio
2. Sync Gradle
3. Start an emulator or connect a physical Android device
4. Press **Run**

---

## Offline exploration flow

If you do not yet have the ESP32/pump connected:

1. open the app
2. browse the dashboard
3. go to settings
4. add or edit connection details
5. return later and test the real device

This makes UI review and navigation testing possible before the hardware is fully ready.

---

## Project goals

This project aims to be:

- simple
- local-first
- easy to understand
- cleanly structured
- practical for a real ESP32 irrigation setup

---

## Future improvements

Potential next steps for the project:

- add screenshots / GIF previews in the README
- improve dashboard offline placeholders
- expand test coverage
- refine visual polish and animations
- connect and validate against a real ESP32 firmware implementation

---

## Author / academic context

This repository appears to be part of an academic / personal project focused on Android development, IoT communication, and local smart irrigation control.

---

## License

No explicit license file is currently present in the repository.
If you plan to publish or share the project publicly, consider adding one.

