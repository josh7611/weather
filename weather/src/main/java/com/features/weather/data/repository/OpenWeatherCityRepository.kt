package com.features.weather.data.repository

import com.features.weather.data.network.GeocodingApiService
import com.features.weather.domain.model.CitySearchResult
import com.features.weather.domain.common.Result
import com.features.weather.domain.repository.CitySearchRepository
import com.features.weather.data.dto.CitySearchDto
import com.features.support.di.OpenWeatherApiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for city search using OpenWeatherMap Geocoding API
 * Following Clean Architecture data layer principles
 */
@Singleton
class OpenWeatherCityRepository @Inject constructor(
    private val geocodingApiService: GeocodingApiService,
    @OpenWeatherApiKey private val apiKey: String
) : CitySearchRepository {

    override suspend fun searchCities(query: String): Result<List<CitySearchResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = geocodingApiService.searchCities(
                    query = query,
                    limit = 10, // Limit to 10 results for better UX
                    apiKey = apiKey
                )

                if (response.isSuccessful) {
                    val cities = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    Result.Success(cities)
                } else {
                    Result.Error(Exception("Failed to search cities: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}

/**
 * Extension function to map CitySearchDto to domain model
 * Following Clean Architecture mapper pattern
 */
private fun CitySearchDto.toDomainModel(): CitySearchResult {
    return CitySearchResult(
        name = name,
        country = country,
        state = null, // OpenWeatherMap geocoding doesn't provide state in basic response
        lat = latitude,
        lon = longitude
    )
}
