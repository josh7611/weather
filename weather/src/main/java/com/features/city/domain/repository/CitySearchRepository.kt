package com.features.city.domain.repository

import com.features.weather.domain.common.Result

interface CitySearchRepository {
    suspend fun searchCities(query: String): Result<List<CitySearchResult>>
}
