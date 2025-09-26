package com.features.weather.domain.usecase

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Weather Use Cases
 * Tests weather-related use cases with mocked repository and covers success/error scenarios
 */
class WeatherUseCasesTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase
    private lateinit var getCurrentWeatherByCoordinatesUseCase: GetCurrentWeatherByCoordinatesUseCase

    // Test data
    private val testWeatherData = WeatherData(
        temperature = 28.5,
        feelsLike = 30.2,
        minTemperature = 22.0,
        maxTemperature = 32.0,
        humidity = 65,
        pressure = 1013,
        description = "Partly cloudy",
        iconCode = "02d",
        city = "Taipei",
        country = "TW",
        windSpeed = 12.3,
        windDirection = 180,
        visibility = 10,
        uvIndex = 6.0
    )

    private val testCityName = "Taipei"
    private val testLatitude = 25.0330
    private val testLongitude = 121.5654

    @Before
    fun setup() {
        weatherRepository = mockk()
        getCurrentWeatherUseCase = GetCurrentWeatherUseCase(weatherRepository)
        getCurrentWeatherByCoordinatesUseCase = GetCurrentWeatherByCoordinatesUseCase(weatherRepository)
    }

    // GetCurrentWeatherUseCase Tests
    @Test
    fun `getCurrentWeatherUseCase returns success when repository succeeds`() = runTest {
        // Given
        coEvery { weatherRepository.getCurrentWeather(testCityName) } returns Result.Success(testWeatherData)

        // When
        val result = getCurrentWeatherUseCase(testCityName)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(testWeatherData, (result as Result.Success).data)
        assertEquals("Taipei", result.data.city)
        assertEquals(28.5, result.data.temperature, 0.01)
        coVerify { weatherRepository.getCurrentWeather(testCityName) }
    }

    @Test
    fun `getCurrentWeatherUseCase returns error when repository fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { weatherRepository.getCurrentWeather(testCityName) } returns Result.Error(exception)

        // When
        val result = getCurrentWeatherUseCase(testCityName)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
        coVerify { weatherRepository.getCurrentWeather(testCityName) }
    }

    @Test
    fun `getCurrentWeatherUseCase returns loading when repository is loading`() = runTest {
        // Given
        coEvery { weatherRepository.getCurrentWeather(testCityName) } returns Result.Loading

        // When
        val result = getCurrentWeatherUseCase(testCityName)

        // Then
        assertTrue(result is Result.Loading)
        coVerify { weatherRepository.getCurrentWeather(testCityName) }
    }

    @Test
    fun `getCurrentWeatherUseCase handles empty city name`() = runTest {
        // Given
        val emptyCityName = ""
        val exception = Exception("City name cannot be empty")
        coEvery { weatherRepository.getCurrentWeather(emptyCityName) } returns Result.Error(exception)

        // When
        val result = getCurrentWeatherUseCase(emptyCityName)

        // Then
        assertTrue(result is Result.Error)
        coVerify { weatherRepository.getCurrentWeather(emptyCityName) }
    }

    @Test
    fun `getCurrentWeatherUseCase handles city not found`() = runTest {
        // Given
        val nonExistentCity = "NonExistentCity"
        val exception = Exception("City not found")
        coEvery { weatherRepository.getCurrentWeather(nonExistentCity) } returns Result.Error(exception)

        // When
        val result = getCurrentWeatherUseCase(nonExistentCity)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("City not found", (result as Result.Error).exception.message)
        coVerify { weatherRepository.getCurrentWeather(nonExistentCity) }
    }

    // GetCurrentWeatherByCoordinatesUseCase Tests
    @Test
    fun `getCurrentWeatherByCoordinatesUseCase returns success when repository succeeds`() = runTest {
        // Given
        coEvery {
            weatherRepository.getCurrentWeatherByCoordinates(testLatitude, testLongitude)
        } returns Result.Success(testWeatherData)

        // When
        val result = getCurrentWeatherByCoordinatesUseCase(testLatitude, testLongitude)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(testWeatherData, (result as Result.Success).data)
        assertEquals("Taipei", result.data.city)
        assertEquals(28.5, result.data.temperature, 0.01)
        coVerify { weatherRepository.getCurrentWeatherByCoordinates(testLatitude, testLongitude) }
    }

    @Test
    fun `getCurrentWeatherByCoordinatesUseCase returns error when repository fails`() = runTest {
        // Given
        val exception = Exception("Invalid coordinates")
        coEvery {
            weatherRepository.getCurrentWeatherByCoordinates(testLatitude, testLongitude)
        } returns Result.Error(exception)

        // When
        val result = getCurrentWeatherByCoordinatesUseCase(testLatitude, testLongitude)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
        coVerify { weatherRepository.getCurrentWeatherByCoordinates(testLatitude, testLongitude) }
    }

    @Test
    fun `getCurrentWeatherByCoordinatesUseCase returns loading when repository is loading`() = runTest {
        // Given
        coEvery {
            weatherRepository.getCurrentWeatherByCoordinates(testLatitude, testLongitude)
        } returns Result.Loading

        // When
        val result = getCurrentWeatherByCoordinatesUseCase(testLatitude, testLongitude)

        // Then
        assertTrue(result is Result.Loading)
        coVerify { weatherRepository.getCurrentWeatherByCoordinates(testLatitude, testLongitude) }
    }

    @Test
    fun `getCurrentWeatherByCoordinatesUseCase handles invalid coordinates`() = runTest {
        // Given
        val invalidLatitude = 999.0
        val invalidLongitude = 999.0
        val exception = Exception("Coordinates out of range")
        coEvery {
            weatherRepository.getCurrentWeatherByCoordinates(invalidLatitude, invalidLongitude)
        } returns Result.Error(exception)

        // When
        val result = getCurrentWeatherByCoordinatesUseCase(invalidLatitude, invalidLongitude)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Coordinates out of range", (result as Result.Error).exception.message)
        coVerify { weatherRepository.getCurrentWeatherByCoordinates(invalidLatitude, invalidLongitude) }
    }

    @Test
    fun `getCurrentWeatherByCoordinatesUseCase handles boundary coordinates`() = runTest {
        // Given - Test boundary coordinates (North Pole)
        val boundaryLatitude = 90.0
        val boundaryLongitude = 180.0
        coEvery {
            weatherRepository.getCurrentWeatherByCoordinates(boundaryLatitude, boundaryLongitude)
        } returns Result.Success(testWeatherData.copy(city = "North Pole"))

        // When
        val result = getCurrentWeatherByCoordinatesUseCase(boundaryLatitude, boundaryLongitude)

        // Then
        assertTrue(result is Result.Success)
        assertEquals("North Pole", (result as Result.Success).data.city)
        coVerify { weatherRepository.getCurrentWeatherByCoordinates(boundaryLatitude, boundaryLongitude) }
    }

    @Test
    fun `getCurrentWeatherByCoordinatesUseCase handles zero coordinates`() = runTest {
        // Given - Test coordinates at origin (0,0) - Gulf of Guinea
        val zeroLatitude = 0.0
        val zeroLongitude = 0.0
        coEvery {
            weatherRepository.getCurrentWeatherByCoordinates(zeroLatitude, zeroLongitude)
        } returns Result.Success(testWeatherData.copy(city = "Gulf of Guinea"))

        // When
        val result = getCurrentWeatherByCoordinatesUseCase(zeroLatitude, zeroLongitude)

        // Then
        assertTrue(result is Result.Success)
        assertEquals("Gulf of Guinea", (result as Result.Success).data.city)
        coVerify { weatherRepository.getCurrentWeatherByCoordinates(zeroLatitude, zeroLongitude) }
    }

    // Edge cases and validation tests
    @Test
    fun `use cases handle special characters in city names`() = runTest {
        // Given
        val cityWithSpecialChars = "São Paulo"
        coEvery { weatherRepository.getCurrentWeather(cityWithSpecialChars) } returns Result.Success(
            testWeatherData.copy(city = cityWithSpecialChars)
        )

        // When
        val result = getCurrentWeatherUseCase(cityWithSpecialChars)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(cityWithSpecialChars, (result as Result.Success).data.city)
        coVerify { weatherRepository.getCurrentWeather(cityWithSpecialChars) }
    }

    @Test
    fun `use cases handle very long city names`() = runTest {
        // Given
        val longCityName = "A".repeat(100) // Very long city name
        val exception = Exception("City name too long")
        coEvery { weatherRepository.getCurrentWeather(longCityName) } returns Result.Error(exception)

        // When
        val result = getCurrentWeatherUseCase(longCityName)

        // Then
        assertTrue(result is Result.Error)
        coVerify { weatherRepository.getCurrentWeather(longCityName) }
    }

    @Test
    fun `use cases handle network timeout scenarios`() = runTest {
        // Given
        val timeoutException = Exception("Request timeout")
        coEvery { weatherRepository.getCurrentWeather(testCityName) } returns Result.Error(timeoutException)

        // When
        val result = getCurrentWeatherUseCase(testCityName)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Request timeout", (result as Result.Error).exception.message)
        coVerify { weatherRepository.getCurrentWeather(testCityName) }
    }
}
