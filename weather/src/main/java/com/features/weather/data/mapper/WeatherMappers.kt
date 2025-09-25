package com.features.weather.data.mapper

import com.features.weather.data.dto.*
import com.features.weather.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Mapper functions to convert DTOs to domain models
 * Following Clean Architecture principles
 */

/**
 * Convert WeatherResponseDto to WeatherData domain model
 */
fun WeatherResponseDto.toDomainModel(): WeatherData {
    return WeatherData(
        temperature = main.temperature,
        feelsLike = main.feelsLike,
        minTemperature = main.minTemperature,
        maxTemperature = main.maxTemperature,
        humidity = main.humidity,
        pressure = main.pressure,
        description = weather.firstOrNull()?.description ?: "",
        iconCode = weather.firstOrNull()?.icon ?: "",
        city = name,
        country = sys.country,
        windSpeed = wind.speed,
        windDirection = wind.direction,
        visibility = visibility
    )
}

/**
 * Convert ForecastResponseDto to WeatherForecast domain model
 */
fun ForecastResponseDto.toDomainModel(): WeatherForecast {
    // Group forecast items by date to get daily forecasts
    val dailyForecasts = forecasts
        .groupBy { it.datetimeText.substring(0, 10) } // Group by date (YYYY-MM-DD)
        .map { (date, items) ->
            val dayItem = items.first()
            val maxTemp = items.maxOfOrNull { it.main.maxTemperature.toDouble() } ?: dayItem.main.maxTemperature
            val minTemp = items.minOfOrNull { it.main.minTemperature.toDouble() } ?: dayItem.main.minTemperature
            val avgHumidity = items.map { it.main.humidity }.average().toInt()
            val maxChanceOfRain = ((items.maxOfOrNull { it.chanceOfRain?.toDouble() ?: 0.0 } ?: 0.0) * 100).toInt()

            DailyWeather(
                date = date,
                dayOfWeek = formatDayOfWeek(date),
                maxTemperature = maxTemp,
                minTemperature = minTemp,
                description = dayItem.weather.firstOrNull()?.description ?: "",
                iconCode = dayItem.weather.firstOrNull()?.icon ?: "",
                humidity = avgHumidity,
                chanceOfRain = maxChanceOfRain
            )
        }
        .take(7) // Take only 7 days

    // Create current weather from the first forecast item if available
    val currentWeather = forecasts.firstOrNull()?.let { firstItem ->
        WeatherData(
            temperature = firstItem.main.temperature,
            feelsLike = firstItem.main.feelsLike,
            minTemperature = firstItem.main.minTemperature,
            maxTemperature = firstItem.main.maxTemperature,
            humidity = firstItem.main.humidity,
            pressure = firstItem.main.pressure,
            description = firstItem.weather.firstOrNull()?.description ?: "",
            iconCode = firstItem.weather.firstOrNull()?.icon ?: "",
            city = city.name,
            country = city.country,
            windSpeed = 0.0, // Not available in forecast response
            windDirection = 0, // Not available in forecast response
            visibility = null
        )
    } ?: WeatherData(
        temperature = 0.0,
        feelsLike = 0.0,
        minTemperature = 0.0,
        maxTemperature = 0.0,
        humidity = 0,
        pressure = 0,
        description = "",
        iconCode = "",
        city = city.name,
        country = city.country,
        windSpeed = 0.0,
        windDirection = 0,
        visibility = null
    )

    return WeatherForecast(
        city = city.name,
        country = city.country,
        currentWeather = currentWeather,
        dailyForecasts = dailyForecasts
    )
}

/**
 * Convert CitySearchDto to City domain model
 */
fun CitySearchDto.toDomainModel(): City {
    return City(
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
        isSelected = false
    )
}

/**
 * Helper function to format day of week from date string
 */
private fun formatDayOfWeek(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Unknown"
    }
}

/**
 * Extension function to convert list of CitySearchDto to domain models
 */
fun List<CitySearchDto>.toDomainModel(): List<City> {
    return map { it.toDomainModel() }
}
