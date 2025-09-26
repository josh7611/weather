package com.features.city.domain.usecase

import com.features.weather.domain.common.Result
import com.features.city.domain.model.City
import com.features.city.domain.repository.CityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for City Use Cases
 * Tests all use cases with mocked repository and covers success/error scenarios
 */
class CityUseCasesTest {

    private lateinit var cityRepository: CityRepository
    private lateinit var getSavedCitiesUseCase: GetSavedCitiesUseCase
    private lateinit var addCityUseCase: AddCityUseCase
    private lateinit var removeCityUseCase: RemoveCityUseCase
    private lateinit var setSelectedCityUseCase: SetSelectedCityUseCase
    private lateinit var getSelectedCityUseCase: GetSelectedCityUseCase

    // Test data
    private val testCities = listOf(
        City(
            name = "Taipei",
            country = "TW",
            latitude = 25.0330,
            longitude = 121.5654,
            isSelected = true,
            lastUsedTime = System.currentTimeMillis()
        ),
        City(
            name = "Tokyo",
            country = "JP",
            latitude = 35.6762,
            longitude = 139.6503,
            isSelected = false,
            lastUsedTime = System.currentTimeMillis() - 86400000
        )
    )

    private val testCity = City(
        name = "London",
        country = "GB",
        latitude = 51.5074,
        longitude = -0.1278,
        isSelected = false,
        lastUsedTime = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        cityRepository = mockk()
        getSavedCitiesUseCase = GetSavedCitiesUseCase(cityRepository)
        addCityUseCase = AddCityUseCase(cityRepository)
        removeCityUseCase = RemoveCityUseCase(cityRepository)
        setSelectedCityUseCase = SetSelectedCityUseCase(cityRepository)
        getSelectedCityUseCase = GetSelectedCityUseCase(cityRepository)
    }

    // GetSavedCitiesUseCase Tests
    @Test
    fun `getSavedCitiesUseCase returns flow of saved cities`() = runTest {
        // Given
        every { cityRepository.getSavedCities() } returns flowOf(testCities)

        // When
        val result = getSavedCitiesUseCase().first()

        // Then
        assertEquals(testCities, result)
        assertEquals(2, result.size)
        assertEquals("Taipei", result[0].name)
        assertEquals("Tokyo", result[1].name)
    }

    @Test
    fun `getSavedCitiesUseCase returns empty list when no cities saved`() = runTest {
        // Given
        every { cityRepository.getSavedCities() } returns flowOf(emptyList())

        // When
        val result = getSavedCitiesUseCase().first()

        // Then
        assertEquals(emptyList<City>(), result)
        assertTrue(result.isEmpty())
    }

    // AddCityUseCase Tests
    @Test
    fun `addCityUseCase returns success when city is added successfully`() = runTest {
        // Given
        coEvery { cityRepository.addCity(testCity) } returns Result.Success(Unit)

        // When
        val result = addCityUseCase(testCity)

        // Then
        assertTrue(result is Result.Success)
        coVerify { cityRepository.addCity(testCity) }
    }

    @Test
    fun `addCityUseCase returns error when city already exists`() = runTest {
        // Given
        val exception = Exception("City already exists in saved list")
        coEvery { cityRepository.addCity(testCity) } returns Result.Error(exception)

        // When
        val result = addCityUseCase(testCity)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
        coVerify { cityRepository.addCity(testCity) }
    }

    @Test
    fun `addCityUseCase returns error when repository throws exception`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { cityRepository.addCity(testCity) } returns Result.Error(exception)

        // When
        val result = addCityUseCase(testCity)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
    }

    // RemoveCityUseCase Tests
    @Test
    fun `removeCityUseCase returns success when city is removed successfully`() = runTest {
        // Given
        val cityName = "London"
        coEvery { cityRepository.removeCity(cityName) } returns Result.Success(Unit)

        // When
        val result = removeCityUseCase(cityName)

        // Then
        assertTrue(result is Result.Success)
        coVerify { cityRepository.removeCity(cityName) }
    }

    @Test
    fun `removeCityUseCase returns error when city not found`() = runTest {
        // Given
        val cityName = "NonExistentCity"
        val exception = Exception("City not found in saved list")
        coEvery { cityRepository.removeCity(cityName) } returns Result.Error(exception)

        // When
        val result = removeCityUseCase(cityName)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
        coVerify { cityRepository.removeCity(cityName) }
    }

    @Test
    fun `removeCityUseCase handles empty city name`() = runTest {
        // Given
        val cityName = ""
        coEvery { cityRepository.removeCity(cityName) } returns Result.Error(Exception("Invalid city name"))

        // When
        val result = removeCityUseCase(cityName)

        // Then
        assertTrue(result is Result.Error)
        coVerify { cityRepository.removeCity(cityName) }
    }

    // SetSelectedCityUseCase Tests
    @Test
    fun `setSelectedCityUseCase returns success when city is set as selected`() = runTest {
        // Given
        val cityName = "Taipei"
        coEvery { cityRepository.setSelectedCity(cityName) } returns Result.Success(Unit)

        // When
        val result = setSelectedCityUseCase(cityName)

        // Then
        assertTrue(result is Result.Success)
        coVerify { cityRepository.setSelectedCity(cityName) }
    }

    @Test
    fun `setSelectedCityUseCase creates new city when city not in saved list`() = runTest {
        // Given
        val cityName = "NewCity"
        coEvery { cityRepository.setSelectedCity(cityName) } returns Result.Success(Unit)

        // When
        val result = setSelectedCityUseCase(cityName)

        // Then
        assertTrue(result is Result.Success)
        coVerify { cityRepository.setSelectedCity(cityName) }
    }

    @Test
    fun `setSelectedCityUseCase returns error when repository fails`() = runTest {
        // Given
        val cityName = "FailingCity"
        val exception = Exception("Database error")
        coEvery { cityRepository.setSelectedCity(cityName) } returns Result.Error(exception)

        // When
        val result = setSelectedCityUseCase(cityName)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
        coVerify { cityRepository.setSelectedCity(cityName) }
    }

    // GetSelectedCityUseCase Tests
    @Test
    fun `getSelectedCityUseCase returns selected city flow`() = runTest {
        // Given
        val selectedCity = testCities[0] // Taipei is selected
        every { cityRepository.observeSelectedCity() } returns flowOf(selectedCity)

        // When
        val result = getSelectedCityUseCase().first()

        // Then
        assertEquals(selectedCity, result)
        assertEquals("Taipei", result?.name)
        assertTrue(result?.isSelected == true)
    }

    @Test
    fun `getSelectedCityUseCase returns null when no city is selected`() = runTest {
        // Given
        every { cityRepository.observeSelectedCity() } returns flowOf(null)

        // When
        val result = getSelectedCityUseCase().first()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `getSelectedCityUseCase observes city selection changes`() = runTest {
        // Given
        val initialCity = testCities[0]
        val updatedCity = testCities[1].copy(isSelected = true)
        every { cityRepository.observeSelectedCity() } returns flowOf(initialCity, updatedCity)

        // When
        val flow = getSelectedCityUseCase()
        val results = mutableListOf<City?>()

        // Collect first two emissions
        flow.collect { city ->
            results.add(city)
            if (results.size == 2) return@collect
        }

        // Then
        assertEquals(2, results.size)
        assertEquals("Taipei", results[0]?.name)
        assertEquals("Tokyo", results[1]?.name)
        assertTrue(results[1]?.isSelected == true)
    }

    // Edge cases and validation tests
    @Test
    fun `use cases handle null parameters appropriately`() = runTest {
        // Test AddCityUseCase with city having null values
        val cityWithNulls = City(
            name = "",
            country = "",
            latitude = 0.0,
            longitude = 0.0,
            isSelected = false,
            lastUsedTime = 0L
        )

        coEvery { cityRepository.addCity(cityWithNulls) } returns Result.Error(Exception("Invalid city data"))

        val result = addCityUseCase(cityWithNulls)
        assertTrue(result is Result.Error)
    }

    @Test
    fun `removeCityUseCase handles special characters in city name`() = runTest {
        // Given
        val cityNameWithSpecialChars = "São Paulo"
        coEvery { cityRepository.removeCity(cityNameWithSpecialChars) } returns Result.Success(Unit)

        // When
        val result = removeCityUseCase(cityNameWithSpecialChars)

        // Then
        assertTrue(result is Result.Success)
        coVerify { cityRepository.removeCity(cityNameWithSpecialChars) }
    }

    @Test
    fun `setSelectedCityUseCase handles case sensitivity`() = runTest {
        // Given
        val cityName = "TAIPEI"
        coEvery { cityRepository.setSelectedCity(cityName) } returns Result.Success(Unit)

        // When
        val result = setSelectedCityUseCase(cityName)

        // Then
        assertTrue(result is Result.Success)
        coVerify { cityRepository.setSelectedCity(cityName) }
    }
}
