package com.features.weather.domain.repository

import com.features.weather.domain.common.Result

interface CitySearchRepository {
    suspend fun searchCities(query: String): Result<List<CitySearchResult>>
}

