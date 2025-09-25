package com.features.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.features.weather.domain.common.Result
import com.features.weather.domain.usecase.*
import com.features.weather.presentation.state.WeatherUiState
import com.features.weather.presentation.state.WeatherUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Weather Screen following Clean Architecture
 * Manages weather data, forecasts, and city selection
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCurrentWeatherByCoordinatesUseCase: GetCurrentWeatherByCoordinatesUseCase,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val getWeatherForecastByCoordinatesUseCase: GetWeatherForecastByCoordinatesUseCase,
    private val getSelectedCityUseCase: GetSelectedCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        observeSelectedCityChanges()
    }

    /**
     * Handle UI events with proper error handling
     */
    fun onEvent(event: WeatherUiEvent) {
        when (event) {
            is WeatherUiEvent.RefreshWeather -> refreshWeather()
            is WeatherUiEvent.ClearError -> clearError()
            is WeatherUiEvent.SelectCity -> loadWeatherForCity(event.cityName)
            is WeatherUiEvent.LoadWeatherForCity -> loadWeatherForCity(event.cityName)
            is WeatherUiEvent.LoadWeatherForCoordinates -> loadWeatherForCoordinates(event.lat, event.lon)
        }
    }

    /**
     * Load weather data for a specific city
     */
    private fun loadWeatherForCity(cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedCity = cityName
            )

            // Load current weather and forecast in parallel
            val currentWeatherDeferred = launch {
                when (val result = getCurrentWeatherUseCase(cityName)) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            currentWeather = result.data,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = result.exception.message,
                            isLoading = false
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }

            val forecastDeferred = launch {
                when (val result = getWeatherForecastUseCase(cityName)) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            weeklyForecast = result.data.dailyForecasts,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        // Don't override current weather error if it exists
                        if (_uiState.value.error == null) {
                            _uiState.value = _uiState.value.copy(
                                error = result.exception.message,
                                isLoading = false
                            )
                        }
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }

            // Wait for both operations to complete
            currentWeatherDeferred.join()
            forecastDeferred.join()
        }
    }

    /**
     * Load weather data for specific coordinates
     */
    private fun loadWeatherForCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load current weather and forecast by coordinates
            val currentWeatherDeferred = launch {
                when (val result = getCurrentWeatherByCoordinatesUseCase(latitude, longitude)) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            currentWeather = result.data,
                            selectedCity = result.data.city,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = result.exception.message,
                            isLoading = false
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }

            val forecastDeferred = launch {
                when (val result = getWeatherForecastByCoordinatesUseCase(latitude, longitude)) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            weeklyForecast = result.data.dailyForecasts,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        // Don't override current weather error if it exists
                        if (_uiState.value.error == null) {
                            _uiState.value = _uiState.value.copy(
                                error = result.exception.message,
                                isLoading = false
                            )
                        }
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }

            // Wait for both operations to complete
            currentWeatherDeferred.join()
            forecastDeferred.join()
        }
    }

    /**
     * Refresh current weather data
     */
    private fun refreshWeather() {
        val currentCity = _uiState.value.selectedCity
        if (currentCity.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            loadWeatherForCity(currentCity)
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    /**
     * Clear error state
     */
    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Observe selected city changes and automatically update weather data
     */
    private fun observeSelectedCityChanges() {
        viewModelScope.launch {
            getSelectedCityUseCase()
                .map { it?.name ?: "Taipei" }
                .distinctUntilChanged()
                .collect { cityName ->
                    if (cityName != _uiState.value.selectedCity) {
                        loadWeatherForCity(cityName)
                    }
                }
        }
    }
}
