package com.features.weather.presentation.state

import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.model.DailyWeather
import com.features.weather.domain.model.City

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
 * UI State for City Selection Screen
 */
data class CitySelectionUiState(
    val savedCities: List<City> = emptyList(),
    val searchResults: List<City> = emptyList(),
    val selectedCity: City? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
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

/**
 * UI Events for City Selection Screen
 */
sealed class CitySelectionUiEvent {
    data class SearchCities(val query: String) : CitySelectionUiEvent()
    data class SelectCity(val city: City) : CitySelectionUiEvent()
    data class AddCity(val city: City) : CitySelectionUiEvent()
    data class RemoveCity(val cityName: String) : CitySelectionUiEvent()
    object ClearSearchResults : CitySelectionUiEvent()
    object ClearError : CitySelectionUiEvent()
}
