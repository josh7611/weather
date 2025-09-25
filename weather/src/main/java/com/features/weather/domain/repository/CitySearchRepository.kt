package com.features.weather.domain.repository

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.CitySearchResult

interface CitySearchRepository {
    suspend fun searchCities(query: String): Result<List<CitySearchResult>>
}

