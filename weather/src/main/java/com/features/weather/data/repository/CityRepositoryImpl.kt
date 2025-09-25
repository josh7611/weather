package com.features.weather.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.features.weather.domain.common.Result
import com.features.weather.domain.model.City
import com.features.weather.domain.repository.CityRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CityRepository for managing saved cities with persistent storage
 * Uses SharedPreferences for data persistence across app restarts
 */
@Singleton
class CityRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CityRepository {

    companion object {
        private const val PREFS_NAME = "weather_cities_prefs"
        private const val KEY_SAVED_CITIES = "saved_cities"
        private const val KEY_SELECTED_CITY = "selected_city"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    // Load saved cities from SharedPreferences on initialization
    private val _savedCities = MutableStateFlow<List<City>>(loadSavedCities())
    private val _selectedCity = MutableStateFlow<City?>(loadSelectedCity())

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

                // Persist to SharedPreferences
                saveCitiesToPreferences(currentCities)

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

                // Persist to SharedPreferences
                saveCitiesToPreferences(currentCities)

                // If removed city was selected, clear selection
                if (_selectedCity.value?.name?.equals(cityName, ignoreCase = true) == true) {
                    _selectedCity.value = null
                    saveSelectedCityToPreferences(null)
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
                val selectedCity = city.copy(isSelected = true, lastUsedTime = currentTime)
                _selectedCity.value = selectedCity

                // Persist to SharedPreferences
                saveCitiesToPreferences(updatedCities)
                saveSelectedCityToPreferences(selectedCity)

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
                val updatedCities = _savedCities.value.map { it.copy(isSelected = false) } + newCity
                _savedCities.value = updatedCities
                _selectedCity.value = newCity

                // Persist to SharedPreferences
                saveCitiesToPreferences(updatedCities)
                saveSelectedCityToPreferences(newCity)

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeSelectedCity(): Flow<City?> {
        return _selectedCity.asStateFlow()
    }

    /**
     * Load saved cities from SharedPreferences
     * Returns default cities if no saved cities exist
     */
    private fun loadSavedCities(): List<City> {
        return try {
            val citiesJson = sharedPreferences.getString(KEY_SAVED_CITIES, null)
            if (citiesJson != null) {
                val type = object : TypeToken<List<City>>() {}.type
                gson.fromJson(citiesJson, type)
            } else {
                // Return default cities with proper timestamps if no saved cities exist
                getDefaultCities()
            }
        } catch (e: Exception) {
            // Fallback to default cities if loading fails
            getDefaultCities()
        }
    }

    /**
     * Load selected city from SharedPreferences
     */
    private fun loadSelectedCity(): City? {
        return try {
            val selectedCityJson = sharedPreferences.getString(KEY_SELECTED_CITY, null)
            if (selectedCityJson != null) {
                gson.fromJson(selectedCityJson, City::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Save cities to SharedPreferences
     */
    private fun saveCitiesToPreferences(cities: List<City>) {
        try {
            val citiesJson = gson.toJson(cities)
            sharedPreferences.edit()
                .putString(KEY_SAVED_CITIES, citiesJson)
                .apply()
        } catch (e: Exception) {
            // Log error but don't crash the app
            e.printStackTrace()
        }
    }

    /**
     * Save selected city to SharedPreferences
     */
    private fun saveSelectedCityToPreferences(city: City?) {
        try {
            if (city != null) {
                val cityJson = gson.toJson(city)
                sharedPreferences.edit()
                    .putString(KEY_SELECTED_CITY, cityJson)
                    .apply()
            } else {
                sharedPreferences.edit()
                    .remove(KEY_SELECTED_CITY)
                    .apply()
            }
        } catch (e: Exception) {
            // Log error but don't crash the app
            e.printStackTrace()
        }
    }

    /**
     * Get default cities with proper timestamps
     */
    private fun getDefaultCities(): List<City> {
        return listOf(
            City("Taipei", "TW", 25.0330, 121.5654, lastUsedTime = System.currentTimeMillis() - 345600000),
            City("New York", "US", 40.7128, -74.0060, lastUsedTime = System.currentTimeMillis() - 86400000),
            City("London", "GB", 51.5074, -0.1278, lastUsedTime = System.currentTimeMillis() - 172800000),
            City("Tokyo", "JP", 35.6762, 139.6503, lastUsedTime = System.currentTimeMillis() - 259200000),
            City("Sydney", "AU", -33.8688, 151.2093, lastUsedTime = System.currentTimeMillis() - 432000000)
        )
    }
}
