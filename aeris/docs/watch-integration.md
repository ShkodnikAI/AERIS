# Apple Watch Integration Guide

## Overview

AERIS Watch app provides real-time heart rate and HRV monitoring during breathing sessions. Data is synced to the iOS app for AI-powered recommendations.

## Features

### Real-time Heart Rate Monitoring
- Continuous HR measurement using HealthKit workout sessions
- Updates sent to iOS app every heartbeat
- Visual pulse animation synced to actual heart rate

### HRV Tracking
- Heart Rate Variability (SDNN) monitoring
- Color-coded status: Red (<30ms), Yellow (30-50ms), Green (>50ms)
- Used for NSI calculation on iOS

### Session Sync
- Watch displays current breathing phase from iOS
- Progress ring shows phase completion
- Quick-start button to begin session from watch

### Independent Operation
- Watch app can run independently
- Stores HR data locally when iOS not reachable
- Syncs data when connection restored

## Setup Requirements

### Xcode Configuration
1. Add watchOS target to project
2. Enable HealthKit capability for both iOS and watchOS
3. Configure Watch Connectivity framework

### Info.plist Keys
```xml
<key>NSHealthShareUsageDescription</key>
<string>AERIS needs heart rate access for personalized recommendations</string>
<key>NSHealthUpdateUsageDescription</key>
<string>AERIS records workout sessions to track breathing practice</string>
```

### Entitlements
```xml
<key>com.apple.developer.healthkit</key>
<true/>
<key>com.apple.developer.healthkit.background-delivery</key>
<true/>
```

## Architecture

```
┌─────────────────┐         ┌─────────────────┐
│   iOS App       │◄───────►│   Watch App     │
│                 │  WC     │                 │
│ ┌─────────────┐ │         │ ┌─────────────┐ │
│ │ Session     │ │ phase   │ │ HealthKit   │ │
│ │ Screen      │─┼─update──┼─│ Manager     │ │
│ └─────────────┘ │         │ └─────────────┘ │
│                 │  HR     │        │        │
│ ┌─────────────┐ │ data    │        ▼        │
│ │ AI Engine   │◄┼─────────┼─ Heart Rate    │
│ │ (NSI/BCI)   │ │         │   Sensor        │
│ └─────────────┘ │         │                 │
└─────────────────┘         └─────────────────┘
```

## Communication Protocol

### iOS → Watch Messages

| Type | Fields | Description |
|------|--------|-------------|
| sessionStart | protocolName | Notify session began |
| sessionUpdate | phase, progress | Current breathing phase |
| sessionEnd | - | Session completed |
| requestHeartRate | - | Request current HR |

### Watch → iOS Messages

| Type | Fields | Description |
|------|--------|-------------|
| heartRateUpdate | heartRate, hrv?, timestamp | Real-time HR data |
| quickStart | timestamp | User tapped start on watch |
| sessionComplete | averageHeartRate, duration | Summary after session |

## Data Flow During Session

1. User starts session on iOS
2. iOS sends `sessionStart` to Watch
3. Watch starts workout session (enables background HR)
4. Watch sends `heartRateUpdate` every beat (~1/sec)
5. iOS updates `sessionUpdate` with phase changes
6. Watch displays phase and progress ring
7. Session ends → Watch sends `sessionComplete` with averages
8. iOS stores session with actual HR data

## Power Optimization

- Workout session used for continuous HR (required by Apple)
- Application context used for non-critical updates
- User info transfer for guaranteed delivery of summaries

## Testing

### Simulator
- Use Xcode's Health Data simulator
- Set simulated HR values in Features → Simulate Heart Rate

### Device
- Pair real Apple Watch with iPhone
- Wear watch properly for accurate readings
- Test during actual movement vs. stationary

## Troubleshooting

### Watch Not Appearing
1. Check Watch app installed via Watch app on iPhone
2. Verify both apps have same App Group
3. Check WCSession activation status

### No Heart Rate Data
1. Ensure HealthKit authorization granted
2. Check workout session started successfully
3. Verify watch is worn properly

### Data Not Syncing
1. Check isReachable status
2. Verify both apps are in foreground initially
3. Use user info transfer for critical data

## File Structure

```
watchApp/
├── AerisWatchApp.swift          # App entry point
├── Info.plist                   # App configuration
├── AerisWatch.entitlements      # HealthKit entitlements
├── Views/
│   └── ContentView.swift        # Main UI
├── Services/
│   ├── HealthKitManager.swift   # HR/HRV monitoring
│   └── WatchConnectivityManager.swift  # iOS communication
└── Models/
    └── (shared with iOS via KMP)
```
