package com.features.weather.domain.model

/**
 * Domain model for current weather data
 * Pure Kotlin class without Android dependencies
 */
data class WeatherData(
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val humidity: Int,
    val pressure: Int,
    val description: String,
    val iconCode: String,
    val city: String,
    val country: String,
    val windSpeed: Double,
    val windDirection: Int,
    val visibility: Int? = null,
    val uvIndex: Double? = null
)

/**
 * Domain model for daily weather forecast
 */
data class DailyWeather(
    val date: String,
    val dayOfWeek: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val chanceOfRain: Int
)

/**
 * Domain model for weather forecast containing daily forecasts
 */
data class WeatherForecast(
    val city: String,
    val country: String,
    val currentWeather: WeatherData,
    val dailyForecasts: List<DailyWeather>
)

/**
 * Domain model for city information
 */
data class City(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isSelected: Boolean = false
)
