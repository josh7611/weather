package com.features.weather.data.repository

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.City
import com.features.weather.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CityRepository for managing saved cities
 * In a real app, this would use Room database for persistence
 * Currently using in-memory storage for demonstration
 */
@Singleton
class CityRepositoryImpl @Inject constructor() : CityRepository {

    // In-memory storage - replace with Room database in production
    private val _savedCities = MutableStateFlow<List<City>>(
        listOf(
            City("New York", "US", 40.7128, -74.0060, lastUsedTime = System.currentTimeMillis() - 86400000), // 1 day ago
            City("London", "GB", 51.5074, -0.1278, lastUsedTime = System.currentTimeMillis() - 172800000), // 2 days ago
            City("Tokyo", "JP", 35.6762, 139.6503, lastUsedTime = System.currentTimeMillis() - 259200000), // 3 days ago
            City("Sydney", "AU", -33.8688, 151.2093, lastUsedTime = System.currentTimeMillis() - 345600000) // 4 days ago
        )
    )

    private val _selectedCity = MutableStateFlow<City?>(null)

    override fun getSavedCities(): Flow<List<City>> {
        return _savedCities.asStateFlow().map { cities ->
            // Sort cities by last used time (most recent first)
            cities.sortedByDescending { it.lastUsedTime }
        }
    }

    override suspend fun addCity(city: City): Result<Unit> {
        return try {
            val currentCities = _savedCities.value.toMutableList()

            // Check if city already exists
            val existingCity = currentCities.find {
                it.name.equals(city.name, ignoreCase = true) &&
                it.country.equals(city.country, ignoreCase = true)
            }

            if (existingCity == null) {
                // Add new city with current timestamp
                val newCity = city.copy(lastUsedTime = System.currentTimeMillis())
                currentCities.add(newCity)
                _savedCities.value = currentCities
                Result.Success(Unit)
            } else {
                Result.Error(Exception("City already exists in saved list"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeCity(cityName: String): Result<Unit> {
        return try {
            val currentCities = _savedCities.value.toMutableList()
            val removed = currentCities.removeAll { it.name.equals(cityName, ignoreCase = true) }

            if (removed) {
                _savedCities.value = currentCities

                // If removed city was selected, clear selection
                if (_selectedCity.value?.name?.equals(cityName, ignoreCase = true) == true) {
                    _selectedCity.value = null
                }

                Result.Success(Unit)
            } else {
                Result.Error(Exception("City not found in saved list"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun setSelectedCity(cityName: String): Result<Unit> {
        return try {
            val currentTime = System.currentTimeMillis()
            val city = _savedCities.value.find { it.name.equals(cityName, ignoreCase = true) }

            if (city != null) {
                // Update selected status and last used time for all cities
                val updatedCities = _savedCities.value.map { existingCity ->
                    if (existingCity.name.equals(cityName, ignoreCase = true)) {
                        // Update the selected city with current timestamp and selected status
                        existingCity.copy(
                            isSelected = true,
                            lastUsedTime = currentTime
                        )
                    } else {
                        // Remove selected status from other cities
                        existingCity.copy(isSelected = false)
                    }
                }

                _savedCities.value = updatedCities
                _selectedCity.value = city.copy(isSelected = true, lastUsedTime = currentTime)
                Result.Success(Unit)
            } else {
                // If city is not in saved list, create it and add it
                val newCity = City(
                    name = cityName,
                    country = "Unknown", // We might not have country info for new cities
                    latitude = 0.0,
                    longitude = 0.0,
                    isSelected = true,
                    lastUsedTime = currentTime
                )

                // Update existing cities to not be selected
                val updatedCities = _savedCities.value.map { it.copy(isSelected = false) }
                _savedCities.value = updatedCities + newCity
                _selectedCity.value = newCity
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getSelectedCity(): Result<City?> {
        return try {
            val selectedCity = _savedCities.value.find { it.isSelected } ?: _selectedCity.value
            Result.Success(selectedCity)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
