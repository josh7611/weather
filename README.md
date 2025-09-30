# Weather Forecast App 🌤️

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)

A modern Android weather application built with **Jetpack Compose** and **Clean Architecture**, showcasing current weather conditions and 7-day forecasts for cities worldwide.

## ✨ Features

- **Current Weather**: Real-time weather data including temperature, humidity, wind speed, and visibility
- **7-Day Forecast**: Extended weather predictions with daily temperature ranges
- **City Search**: Search and save multiple cities for quick access
- **Modern UI**: Material 3 design system with responsive layouts
- **Offline Support**: Graceful handling of network connectivity issues
- **Real-time Updates**: Automatic refresh capabilities

## 🎬 Demo

### App in Action
<video width="300" controls>
  <source src="docs/demo/weather_app_demo.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>

*Demo showing weather app functionality including current weather display, city selection, and 7-day forecast navigation*

## 📱 Screenshots

### Main Weather Screen
<img src="docs/images/weather_app_main_screen.png" alt="Weather App Main Screen" width="300"/>

*Weather forecast display showing current conditions for New Taipei with 30°C temperature, weather details, and 7-day forecast*

## 🏗️ Architecture

This project follows **Clean Architecture** principles with three distinct layers:

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│  (UI, ViewModels, Compose Screens)  │
├─────────────────────────────────────┤
│            Domain Layer             │
│   (Use Cases, Models, Repositories) │
├─────────────────────────────────────┤
│             Data Layer              │
│  (API Services, DTOs, Repositories) │
└─────────────────────────────────────┘
```

### Key Components

- **MVVM Pattern**: ViewModel manages UI state with StateFlow
- **Dependency Injection**: Hilt for clean dependency management
- **Reactive Programming**: Coroutines and Flow for async operations
- **Modular Design**: Separate weather module for feature isolation

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern declarative UI toolkit
- **Hilt** - Dependency injection framework
- **Coroutines & Flow** - Asynchronous programming

### Architecture Components
- **ViewModel** - UI state management
- **Navigation Compose** - Type-safe navigation
- **StateFlow** - Reactive state management

### Network & Data
- **Retrofit** - HTTP client for API communication
- **OkHttp** - Network interceptor and logging
- **Gson** - JSON serialization/deserialization

### Testing
- **JUnit 4** - Unit testing framework
- **MockK** - Mocking library for Kotlin
- **Coroutines Test** - Testing coroutines
- **Turbine** - Testing Flow streams

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- Android SDK 26 (Android 8.0) or higher
- OpenWeatherMap API key

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/weather-app.git
   cd weather-app
   ```

2. **Get an API key**
   - Sign up at [OpenWeatherMap](https://openweathermap.org/api)
   - Get your free API key

3. **Configure API key**
   - Create `environment.properties` file in the root directory
   - Add your API key:
   ```properties
   WEATHER_API_KEY=your_api_key_here
   ```
   **Note**: Ensure `environment.properties` is added to your `.gitignore` file to prevent exposing your API key.

4. **Build and run**
   - Open project in Android Studio
   - Sync project with Gradle files
   - Run the app on device/emulator

## 📁 Project Structure

```
.
├── app/              # Main application module (DI, navigation, application class)
├── weather/          # Weather feature module (UI, domain, data)
└── build-logic/      # (Optional) For shared build configuration
```

## 🧪 Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

Current test coverage:
- **Domain Layer**: ✅ Use cases with comprehensive scenarios
- **Data Layer**: 🔄 Repository tests (planned)
- **Presentation Layer**: 🔄 ViewModel tests (planned)

## 🔧 Configuration

### Build Variants
- **Debug**: Development build with logging enabled
- **Release**: Production build with code optimization

### API Configuration
The app uses OpenWeatherMap API with the following endpoints:
- Current weather: `/weather`
- 5-day forecast: `/forecast`
- Geocoding: `/geo/1.0/direct`

## 📋 Features Roadmap

### Implemented ✅
- [x] Current weather display
- [x] City search and selection
- [x] 7-day weather forecast
- [x] Error handling with user feedback
- [x] Material 3 design system
- [x] Unit tests for core business logic

### Planned 🔄
- [ ] Local data caching (Room database)
- [ ] Widget support
- [ ] Weather notifications
- [ ] Location-based weather
- [ ] Multiple theme support
- [ ] Weather maps integration

## 🔧 Technical Improvements Todo

### Local Caching Implementation
- [ ] **Room Database Setup**
- [ ] **Repository Pattern Enhancement**
- [ ] **Data Synchronization Strategy**

### Network State Monitoring
- [ ] **Connectivity Manager Integration**
- [ ] **Network Status UI Indicators**

### Enhanced Testing Coverage
- [ ] **Repository Testing**
- [ ] **ViewModel Testing**
- [ ] **Compose UI Testing**

### Performance Optimizations
- [ ] **Image Caching**
- [ ] **Pagination Implementation**
- [ ] **Compose Optimizations**

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Contact

Josh Hsu - josh7611@gmail.com

## 🙏 Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for providing weather data API
- [Material Design](https://material.io/) for design guidelines
- [Android Developers](https://developer.android.com/) for excellent documentation

---

*Built with ❤️ for learning modern Android development*