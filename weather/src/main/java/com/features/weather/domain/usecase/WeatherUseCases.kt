package com.features.weather.domain.usecase

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.repository.WeatherRepository
import javax.inject.Inject

/**
 * Use case for getting current weather data
 * Single responsibility: fetch current weather for a city
 */
class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): Result<WeatherData> {
        return weatherRepository.getCurrentWeather(cityName)
    }
}

/**
 * Use case for getting current weather by coordinates
 */
class GetCurrentWeatherByCoordinatesUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<WeatherData> {
        return weatherRepository.getCurrentWeatherByCoordinates(latitude, longitude)
    }
}
