package com.features.weather.domain.repository

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.City
import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for weather-related operations
 * Defines the contract for weather data access
 */
interface WeatherRepository {
    /**
     * Get current weather for a specific city
     */
    suspend fun getCurrentWeather(cityName: String): Result<WeatherData>

    /**
     * Get current weather by coordinates
     */
    suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<WeatherData>

    /**
     * Get 7-day weather forecast for a city
     */
    suspend fun getWeatherForecast(cityName: String): Result<WeatherForecast>

    /**
     * Get weather forecast by coordinates
     */
    suspend fun getWeatherForecastByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<WeatherForecast>
}

/**
 * Repository interface for city management
 */
interface CityRepository {
    /**
     * Get all saved cities as a Flow for reactive updates
     */
    fun getSavedCities(): Flow<List<City>>

    /**
     * Add a city to saved list
     */
    suspend fun addCity(city: City): Result<Unit>

    /**
     * Remove a city from saved list
     */
    suspend fun removeCity(cityName: String): Result<Unit>

    /**
     * Set selected city
     */
    suspend fun setSelectedCity(cityName: String): Result<Unit>

    /**
     * Get currently selected city
     */
    suspend fun getSelectedCity(): Result<City?>
}
