# OpenWeatherMap Geocoding API Migration Guide

This project has been updated to use OpenWeatherMap's Geocoding API instead of Google Places API for city search functionality.

## Changes Made

### 1. New API Service
- Created `GeocodingApiService` for OpenWeatherMap's Geocoding API
- Separated from `WeatherApiService` due to different base URLs
- Uses endpoint: `https://api.openweathermap.org/geo/1.0/direct`

### 2. New Repository Implementation
- Created `OpenWeatherCityRepository` to replace `PlacesCityRepository`
- Uses OpenWeatherMap's Geocoding API instead of Google Places API
- Maps `CitySearchDto` to domain `CitySearchResult`

### 3. Updated Dependency Injection
- Added separate Retrofit instances for weather and geocoding APIs
- Added API key provider with `@OpenWeatherApiKey` qualifier
- Bound new repository in both modules

## Setup Instructions

### 1. Get OpenWeatherMap API Key
1. Sign up at [OpenWeatherMap](https://openweathermap.org/api)
2. Get your free API key from the dashboard
3. Note: The same API key works for both weather and geocoding APIs

### 2. Configure API Key
Update the API key in `/Users/josh.yc.hsu/Projects/weather/weather/src/main/java/com/features/support/di/WeatherModule.kt`:

```kotlin
@Provides
@Singleton
@OpenWeatherApiKey
fun provideOpenWeatherApiKey(): String {
    return "YOUR_ACTUAL_API_KEY_HERE"
}
```

**For Production**: Consider using:
- `BuildConfig.OPENWEATHER_API_KEY` 
- Android Keystore
- Encrypted SharedPreferences
- Environment variables

### 3. Remove Google Places Dependencies
You can now remove these dependencies from your `build.gradle.kts`:
```kotlin
// Remove these lines
implementation("com.google.android.libraries.places:places:x.x.x")
```

## API Usage Comparison

### Before (Google Places)
```kotlin
// Used Google Places AutoComplete API
// Required Google API key and Places SDK
// Returned place predictions with place IDs
```

### After (OpenWeatherMap Geocoding)
```kotlin
// Uses OpenWeatherMap Geocoding API
// Same API key as weather data
// Direct lat/lon coordinates
// Up to 5 results per query (configurable)
```

## Benefits of Migration

1. **Single API Provider**: Use one API key for weather and location data
2. **Cost Effective**: OpenWeatherMap geocoding is included in free tier
3. **Consistent Data**: Weather and location data from same provider
4. **Better Integration**: Coordinates directly usable for weather API calls

## Testing

Test the city search functionality:
1. Start the app
2. Navigate to city selection
3. Type a city name (e.g., "London", "New York")
4. Verify results show with coordinates
5. Verify weather data loads for selected cities

## Troubleshooting

### API Key Issues
- Ensure API key is valid and active
- Check API key has geocoding permissions enabled
- Verify network connectivity

### No Search Results
- API has rate limits (60 calls/minute for free tier)
- Some city names might not be found - try variations
- Check API response in logs for error details

### Build Errors
- Clean and rebuild project
- Ensure all new files are properly imported
- Check Hilt annotation processing is working
