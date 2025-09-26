package com.features.city.domain.usecase

import com.features.weather.domain.common.Result
import com.features.city.domain.repository.CitySearchResult
import com.features.city.domain.repository.CitySearchRepository
import javax.inject.Inject

/**
 * Use case for searching cities
 * Following Clean Architecture and single responsibility principle
 */
class SearchCitiesUseCase @Inject constructor(
    private val citySearchRepository: CitySearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<CitySearchResult>> {
        return if (query.isBlank()) {
            Result.Success(emptyList())
        } else {
            citySearchRepository.searchCities(query.trim())
        }
    }
}
