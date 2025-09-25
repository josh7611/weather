package com.features.weather.domain.usecase

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.WeatherForecast
import com.features.weather.domain.repository.CitySearchResult
import com.features.weather.domain.repository.WeatherRepository
import com.features.weather.domain.repository.CitySearchRepository
import javax.inject.Inject

/**
 * Use case for getting weather forecast
 * Single responsibility: fetch 7-day weather forecast for a city
 */
class GetWeatherForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): Result<WeatherForecast> {
        return weatherRepository.getWeatherForecast(cityName)
    }
}

/**
 * Use case for getting weather forecast by coordinates
 */
class GetWeatherForecastByCoordinatesUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<WeatherForecast> {
        return weatherRepository.getWeatherForecastByCoordinates(latitude, longitude)
    }
}

/**
 * Use case for searching cities using OpenWeatherMap Geocoding API
 */
class SearchCitiesUseCase @Inject constructor(
    private val citySearchRepository: CitySearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<CitySearchResult>> {
        return citySearchRepository.searchCities(query)
    }
}
