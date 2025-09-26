package com.features.weather.presentation.state

import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.model.DailyWeather
import com.features.city.domain.model.City
import com.features.city.domain.repository.CitySearchResult

/**
 * UI State for Weather Screen following Clean Architecture
 * Represents all possible states of the weather feature
 */
data class WeatherUiState(
    val currentWeather: WeatherData? = null,
    val weeklyForecast: List<DailyWeather> = emptyList(),
    val selectedCity: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * UI Events for Weather Screen
 * Sealed class representing all possible user interactions
 */
sealed class WeatherUiEvent {
    object RefreshWeather : WeatherUiEvent()
    object ClearError : WeatherUiEvent()
    data class SelectCity(val cityName: String) : WeatherUiEvent()
    data class LoadWeatherForCity(val cityName: String) : WeatherUiEvent()
    data class LoadWeatherForCoordinates(val lat: Double, val lon: Double) : WeatherUiEvent()
}
