# GitHub Copilot Instructions for Weather Forecast Android App

## Project Overview
This is an Android weather forecast application built with Kotlin, Jetpack Compose, and Clean Architecture. The app displays current day weather, weekly forecasts, and allows users to select different cities.

## Architecture Guidelines

### Clean Architecture Layers
- **Presentation Layer**: Jetpack Compose UI, ViewModels, UI States
- **Domain Layer**: Use cases, repositories interfaces, domain models
- **Data Layer**: Repository implementations, API services, local database, DTOs

### Module Structure
- **app**: Main application module with navigation and dependency injection
- **feature-weather**: Feature module containing weather-related functionality
- **core**: Shared utilities, network configuration, common models

## Code Style Guidelines

### Kotlin Conventions
- Use **camelCase** for variables and function names
- Use **PascalCase** for class names and interfaces
- Use **SCREAMING_SNAKE_CASE** for constants
- Prefer **data classes** for models and states
- Use **sealed classes** for representing states and results
- Leverage **extension functions** for utility operations

### Jetpack Compose Patterns
- Use **Composable functions** with proper naming (PascalCase)
- Implement **state hoisting** for reusable composables
- Use **remember** and **rememberSaveable** appropriately
- Follow **unidirectional data flow** patterns
- Implement **proper preview functions** for all composables

### Clean Architecture Specific
- **Use Cases** should have single responsibility and be testable
- **Repository interfaces** in domain layer, implementations in data layer
- **Domain models** should be pure Kotlin classes without Android dependencies
- **ViewModels** should only contain UI-related logic and state management

## Error Handling Patterns

### Network Operations
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Usage in repository
suspend fun getWeatherForecast(city: String): Result<WeatherForecast> {
    return try {
        val response = weatherApi.getForecast(city)
        Result.Success(response.toDomainModel())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

### UI State Management
```kotlin
data class WeatherUiState(
    val currentWeather: WeatherData? = null,
    val weeklyForecast: List<DailyWeather> = emptyList(),
    val selectedCity: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
```

## Dependency Injection with Hilt

### Module Organization
- **NetworkModule**: API services, OkHttp, Retrofit configuration
- **RepositoryModule**: Repository implementations binding
- **DatabaseModule**: Room database and DAO providers
- **UseCaseModule**: Use case implementations

### Annotations Usage
- Use **@HiltAndroidApp** for Application class
- Use **@AndroidEntryPoint** for Activities, Fragments, ViewModels
- Use **@Singleton** for app-wide dependencies
- Use **@ViewModelScoped** for ViewModel dependencies

## Coroutines and Async Operations

### Repository Layer
- Use **suspend functions** for API calls
- Implement **flow-based** data streams for reactive updates
- Use **withContext(Dispatchers.IO)** for network operations
- Handle **cancellation** properly with structured concurrency

### ViewModel Layer
- Launch coroutines in **viewModelScope**
- Use **StateFlow** and **SharedFlow** for state management
- Implement **proper error handling** with try-catch blocks
- Use **combine** operators for multiple data sources

## Testing Guidelines

### Unit Tests
- Test **use cases** with mocked repositories
- Test **ViewModels** with fake repositories and test dispatchers
- Use **MockK** for mocking dependencies
- Test **error scenarios** and edge cases

### UI Tests
- Write **Composable tests** using ComposeTestRule
- Test **user interactions** and state changes
- Use **semantics** for accessibility and testing
- Mock **data sources** for consistent test results

## API Integration

### Weather API Best Practices
- Use **data classes** for API responses with proper serialization
- Implement **mapper functions** to convert DTOs to domain models
- Add **API key management** through BuildConfig or secure storage
- Implement **caching strategy** for offline functionality
- Handle **rate limiting** and **network timeouts**

### Example API Integration
```kotlin
@GET("weather")
suspend fun getCurrentWeather(
    @Query("q") city: String,
    @Query("appid") apiKey: String
): WeatherResponse

// Mapper
fun WeatherResponse.toDomainModel(): WeatherData {
    return WeatherData(
        temperature = main.temp,
        description = weather.first().description,
        humidity = main.humidity,
        city = name
    )
}
```

## UI/UX Guidelines

### Jetpack Compose UI
- Follow **Material Design 3** guidelines

## Performance Considerations
- Minimize object allocation in video frame processing loops
- Use **object pooling** for frequently created objects like VideoFrame
- Implement proper **bitmap recycling** to avoid memory leaks
- Use **background threads** for heavy operations like image processing

## Security Guidelines
- Always validate input parameters
- Use **secure storage** for sensitive data
- Implement **proper certificate validation** for network connections
- Follow **Android security best practices** for permissions and data access
