package com.features.weather.domain.repository

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.model.WeatherForecast

interface WeatherRepository {
    suspend fun getCurrentWeather(cityName: String): Result<WeatherData>
    suspend fun getCurrentWeatherByCoordinates(latitude: Double, longitude: Double): Result<WeatherData>
    suspend fun getWeatherForecast(cityName: String): Result<WeatherForecast>
    suspend fun getWeatherForecastByCoordinates(latitude: Double, longitude: Double): Result<WeatherForecast>
}

