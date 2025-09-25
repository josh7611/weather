package com.features.weather.domain.usecase

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.City
import com.features.weather.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting saved cities
 * Single responsibility: retrieve all saved cities as reactive stream
 */
class GetSavedCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    operator fun invoke(): Flow<List<City>> {
        return cityRepository.getSavedCities()
    }
}

/**
 * Use case for adding a city to saved list
 */
class AddCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(city: City): Result<Unit> {
        return cityRepository.addCity(city)
    }
}

/**
 * Use case for removing a city from saved list
 */
class RemoveCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(cityName: String): Result<Unit> {
        return cityRepository.removeCity(cityName)
    }
}

/**
 * Use case for setting selected city
 */
class SetSelectedCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(cityName: String): Result<Unit> {
        return cityRepository.setSelectedCity(cityName)
    }
}

/**
 * Use case for getting currently selected city as a Flow
 */
class GetSelectedCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    operator fun invoke(): Flow<City?> {
        return cityRepository.observeSelectedCity()
    }
}
