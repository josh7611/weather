package com.features.weather.data.repository

import com.features.weather.data.mapper.toDomainModel
import com.features.weather.domain.common.Result
import com.features.weather.domain.model.City
import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.model.WeatherForecast
import com.features.weather.domain.repository.WeatherRepository
import com.features.weather.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WeatherRepository following Clean Architecture
 * Handles weather-related data operations with proper error handling
 */
@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {

    companion object {
        private const val API_KEY = "YOUR_API_KEY_HERE" // TODO: Move to BuildConfig or secure storage
    }

    override suspend fun getCurrentWeather(cityName: String): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getCurrentWeather(cityName, API_KEY)
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!.toDomainModel())
                } else {
                    Result.Error(Exception("Failed to fetch weather data: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getCurrentWeatherByCoordinates(
                    latitude, longitude, API_KEY
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!.toDomainModel())
                } else {
                    Result.Error(Exception("Failed to fetch weather data: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getWeatherForecast(cityName: String): Result<WeatherForecast> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getWeatherForecast(cityName, API_KEY)
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!.toDomainModel())
                } else {
                    Result.Error(Exception("Failed to fetch forecast data: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getWeatherForecastByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<WeatherForecast> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getWeatherForecastByCoordinates(
                    latitude, longitude, API_KEY
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!.toDomainModel())
                } else {
                    Result.Error(Exception("Failed to fetch forecast data: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun searchCities(query: String): Result<List<City>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.searchCities(query, 5, API_KEY)
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!.toDomainModel())
                } else {
                    Result.Error(Exception("Failed to search cities: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}
