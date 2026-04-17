# SmartIrrigation

> A local Wi-Fi Android application for monitoring and controlling an ESP32-based irrigation system directly over the same LAN.

## Project Description

SmartIrrigation is an Android application built for a local-only irrigation setup where an Android phone communicates directly with an ESP32 over HTTP through the same Wi-Fi router. The app was designed to keep the architecture simple, practical, and easy to maintain: there is no cloud backend, no external API, no login system, and no SQL database involved. All control and status operations are performed by the ESP32, while the Android app acts as a clean client interface for configuration, monitoring, and manual interaction.

The project uses **Kotlin**, **Jetpack Compose**, **Material 3**, **MVVM**, **Repository**, and **StateFlow** in a single-activity Android architecture. It allows the user to configure the ESP32 IPv4 address and port, test the device connection, read current humidity, inspect the irrigation mode, view the pump state, set the target humidity, and switch between automatic and manual behavior. The app also stores local preferences such as host, port, theme mode, and polling interval using **Preferences DataStore**.

This project is useful both as a real control interface for an ESP32 watering system and as a clean academic/mobile development example that demonstrates local LAN communication between Android and embedded hardware. The application also supports browsing the interface even when the physical pump is not connected yet, so the screens, navigation, and settings can still be reviewed before the hardware is ready.

---

## Main Features

- Configure the ESP32 **IPv4 address** and **port**
- Test connectivity with `GET /status`
- View current:
  - soil humidity
  - target humidity
  - pump state
  - irrigation mode
  - optional device name
- Set a new target humidity value
- Switch between **AUTO** and **MANUAL** modes
- Start and stop the pump manually
- Persist local settings with **Preferences DataStore**
- Use **System / Light / Dark** theme modes
- Navigate through the app even if the ESP32 is temporarily unavailable

---

## Architecture Overview

This project follows a **single-activity** architecture and keeps everything inside one Android app module.

- **UI:** Jetpack Compose + Material 3
- **Navigation:** Navigation Compose
- **Presentation:** MVVM
- **State management:** StateFlow
- **Data layer:** Repository pattern
- **Dependency management:** manual wiring through an `AppContainer`
- **Networking:** Retrofit + OkHttp + Kotlin Serialization
- **Local persistence:** Preferences DataStore

Package structure used in the app module:

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

## Local Communication Model

SmartIrrigation works only in a **local Wi-Fi** scenario.

- the Android phone and the ESP32 must be connected to the **same router**
- the application sends HTTP requests directly to the ESP32
- the ESP32 is the **only backend/server**
- status updates are refreshed through **polling**

The project does **not** use:

- cloud sync
- Firebase
- login or registration
- external backend services
- Room or SQL database
- WebSocket
- BLE

---

## ESP32 API Contract

Base URL format:

```text
http://<saved_ip>:<saved_port>/
```

Defaults:

```text
port = 80
pollingIntervalMs = 2500
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

## Setup and Build

This section includes the full setup flow needed to build the project successfully.

### 1. Prerequisites

Make sure the following tools are installed:

- **Android Studio** with Android support
- **Android SDK** installed from the SDK Manager
- **JDK 11** or a compatible JDK configured for Gradle
- A Windows terminal or PowerShell if you want to build from the command line

### 2. Clone or open the project

Open the project root folder:

```text
SmartIrrigation/
```

The project currently uses:

- **AGP:** `9.0.1`
- **Kotlin:** `2.0.21`
- **Compile SDK:** `36`
- **Target SDK:** `36`
- **Min SDK:** `33`
- **Java compatibility:** `11`

These values come from:

- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `settings.gradle.kts`

### 3. Verify local configuration

Before building, check the following:

1. Android Studio has downloaded the required SDK platform and build tools.
2. Gradle sync completes successfully.
3. Your JDK is available to Gradle.
4. If the project uses `local.properties`, make sure the Android SDK path is correct on your machine.

Typical `local.properties` example on Windows:

```properties
sdk.dir=C\:\Users\YourUser\AppData\Local\Android\Sdk
```

If Gradle cannot find Java, configure `JAVA_HOME` in PowerShell:

```powershell
$env:JAVA_HOME="C:\Path\To\JDK-11"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

### 4. Network-related configuration already included in the app

The app is already configured for local HTTP communication with the ESP32.

- `android.permission.INTERNET`
- `android.permission.ACCESS_NETWORK_STATE`
- cleartext HTTP enabled in `AndroidManifest.xml`
- `network_security_config.xml` used for local LAN communication

### 5. Build from Android Studio

1. Open the project in Android Studio.
2. Wait for **Gradle Sync** to finish.
3. Open the **Build** menu.
4. Choose **Make Project** or run the `app` configuration.

### 6. Build from terminal

From the project root, run:

```powershell
.\gradlew.bat clean --console=plain
.\gradlew.bat assembleDebug --console=plain
```

This generates the debug APK for the app module.

### 7. Run unit tests

To execute unit tests:

```powershell
.\gradlew.bat testDebugUnitTest --console=plain
```

---

## Running

This section explains how to run the application after it has been built.

### Option A: Run from Android Studio

1. Open the project in Android Studio.
2. Wait until Gradle sync is complete.
3. Connect a physical Android phone with USB debugging enabled, or start an Android emulator.
4. In the top toolbar, select the **app** run configuration.
5. Choose a target device.
6. Press **Run**.

If Android Studio shows **No target device found**, it means there is no emulator running and no physical Android device currently connected and recognized by ADB.

### Option B: Install and run on a physical Android phone

This option is recommended for real ESP32 testing because the phone must usually be connected to the same Wi-Fi network as the ESP32.

Steps:

1. Enable **Developer options** on the phone.
2. Enable **USB debugging**.
3. Connect the phone to the computer with a USB cable.
4. Accept the debugging authorization prompt on the phone.
5. Run the app from Android Studio.
6. After launch, enter the ESP32 IPv4 address and port in the connection screen.
7. Test the connection.

### Option C: Run with an emulator

You can also use an emulator for UI validation and navigation testing.

Steps:

1. Open **Device Manager** in Android Studio.
2. Create a virtual device if none exists.
3. Start the emulator.
4. Run the `app` configuration.

Note: emulator use is useful for interface review, but real ESP32 connectivity depends on your network setup and may be easier to validate on a physical phone connected to the same Wi-Fi network.

### First-time in-app flow

After the app opens:

1. Go to the **Connect** screen.
2. Enter the ESP32 IPv4 address, for example `192.168.1.55`.
3. Enter the port, usually `80`.
4. Press **Test connection**.
5. If the test succeeds, press **Save and continue**.
6. Open the **Dashboard** to monitor humidity, mode, and pump state.
7. Use **Settings** to adjust the theme or polling interval.

### Running without the pump connected yet

The application can still be opened and navigated even if the ESP32 is not available yet.

You can:

- open the side navigation / screens
- inspect the UI
- edit saved connection values
- configure appearance settings
- review the dashboard layout in a disconnected state

This makes development, presentation, and UI verification possible before the hardware is fully connected.

---

## Business Rules

- `targetHumidity` must be between `0` and `100`
- humidity is treated as an integer percentage between `0` and `100`
- the available modes are `AUTO` and `MANUAL`
- humidity status is derived as follows:
  - `DRY` if `humidity < targetHumidity - 3`
  - `GOOD` if `humidity` is between `targetHumidity - 3` and `targetHumidity + 3` inclusive
  - `WET` if `humidity > targetHumidity + 3`

---

## Local Persistence

The app persists only local settings:

- `host`
- `port`
- `appThemeMode`
- `pollingIntervalMs`

Default values:

```text
host = ""
port = 80
appThemeMode = SYSTEM
pollingIntervalMs = 2500
```

---

## Project Goal

The main goal of SmartIrrigation is to provide a simple, elegant, and functional Android client for an ESP32 irrigation system running on a local Wi-Fi network. The project emphasizes clarity, practical architecture, and direct communication with embedded hardware. It is intentionally focused on a small real-world use case instead of trying to become a cloud platform or a multi-user management system.
