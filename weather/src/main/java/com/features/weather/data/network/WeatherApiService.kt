package com.features.weather.data.network

import com.features.weather.data.dto.WeatherResponseDto
import com.features.weather.data.dto.ForecastResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API service interface for weather-related network operations
 * Following Clean Architecture data layer principles
 */
interface WeatherApiService {

    /**
     * Get current weather by city name
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponseDto>

    /**
     * Get current weather by coordinates
     */
    @GET("weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponseDto>

    /**
     * Get 5-day weather forecast (every 3 hours)
     */
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponseDto>

    /**
     * Get weather forecast by coordinates
     */
    @GET("forecast")
    suspend fun getWeatherForecastByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponseDto>

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }
}
