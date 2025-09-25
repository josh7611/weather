package com.features.weather.data.network

import com.features.weather.data.dto.CitySearchDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API service interface for OpenWeatherMap Geocoding API
 * Separate from WeatherApiService due to different base URL
 */
interface GeocodingApiService {

    /**
     * Search cities by name using OpenWeatherMap Geocoding API
     */
    @GET("geo/1.0/direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Response<List<CitySearchDto>>

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/"
    }
}
