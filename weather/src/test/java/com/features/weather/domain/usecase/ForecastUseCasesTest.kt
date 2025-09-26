package com.features.weather.domain.usecase

import com.features.weather.domain.common.Result
import com.features.weather.domain.model.WeatherForecast
import com.features.weather.domain.model.DailyWeather
import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.repository.WeatherRepository
import com.features.weather.domain.repository.CitySearchRepository
import com.features.weather.domain.repository.CitySearchResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Forecast Use Cases
 * Tests forecast and city search use cases with mocked repositories and covers success/error scenarios
 */
class ForecastUseCasesTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var citySearchRepository: CitySearchRepository
    private lateinit var getWeatherForecastUseCase: GetWeatherForecastUseCase
    private lateinit var getWeatherForecastByCoordinatesUseCase: GetWeatherForecastByCoordinatesUseCase
    private lateinit var searchCitiesUseCase: SearchCitiesUseCase

    // Test data
    private val testCurrentWeather = WeatherData(
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

    private val testDailyWeatherList = listOf(
        DailyWeather(
            date = "2024-01-15",
            dayOfWeek = "Monday",
            maxTemperature = 25.0,
            minTemperature = 18.0,
            description = "Sunny",
            humidity = 60,
            iconCode = "01d",
            chanceOfRain = 10
        ),
        DailyWeather(
            date = "2024-01-16",
            dayOfWeek = "Tuesday",
            maxTemperature = 23.0,
            minTemperature = 16.0,
            description = "Partly cloudy",
            humidity = 65,
            iconCode = "02d",
            chanceOfRain = 20
        ),
        DailyWeather(
            date = "2024-01-17",
            dayOfWeek = "Wednesday",
            maxTemperature = 20.0,
            minTemperature = 14.0,
            description = "Rainy",
            humidity = 80,
            iconCode = "09d",
            chanceOfRain = 85
        )
    )

    private val testWeatherForecast = WeatherForecast(
        city = "Taipei",
        country = "TW",
        currentWeather = testCurrentWeather,
        dailyForecasts = testDailyWeatherList
    )

    private val testCitySearchResults = listOf(
        CitySearchResult(
            name = "Taipei",
            country = "TW",
            state = null,
            lat = 25.0330,
            lon = 121.5654
        ),
        CitySearchResult(
            name = "Taichung",
            country = "TW",
            state = null,
            lat = 24.1477,
            lon = 120.6736
        )
    )

    private val testCityName = "Taipei"
    private val testLatitude = 25.0330
    private val testLongitude = 121.5654
    private val testSearchQuery = "Tai"

    @Before
    fun setup() {
        weatherRepository = mockk()
        citySearchRepository = mockk()
        getWeatherForecastUseCase = GetWeatherForecastUseCase(weatherRepository)
        getWeatherForecastByCoordinatesUseCase = GetWeatherForecastByCoordinatesUseCase(weatherRepository)
        searchCitiesUseCase = SearchCitiesUseCase(citySearchRepository)
    }

    // GetWeatherForecastUseCase Tests
    @Test
    fun `getWeatherForecastUseCase returns success when repository succeeds`() = runTest {
        // Given
        coEvery { weatherRepository.getWeatherForecast(testCityName) } returns Result.Success(testWeatherForecast)

        // When
        val result = getWeatherForecastUseCase(testCityName)

        // Then
        assertTrue(result is Result.Success)
        val forecast = (result as Result.Success).data
        assertEquals(testWeatherForecast, forecast)
        assertEquals("Taipei", forecast.city)
        assertEquals(3, forecast.dailyForecasts.size)
        assertEquals("2024-01-15", forecast.dailyForecasts[0].date)
        assertEquals(25.0, forecast.dailyForecasts[0].maxTemperature, 0.01)
        coVerify { weatherRepository.getWeatherForecast(testCityName) }
    }

    @Test
    fun `getWeatherForecastUseCase returns error when repository fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { weatherRepository.getWeatherForecast(testCityName) } returns Result.Error(exception)

        // When
        val result = getWeatherForecastUseCase(testCityName)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
        coVerify { weatherRepository.getWeatherForecast(testCityName) }
    }

    @Test
    fun `getWeatherForecastUseCase returns loading when repository is loading`() = runTest {
        // Given
        coEvery { weatherRepository.getWeatherForecast(testCityName) } returns Result.Loading

        // When
        val result = getWeatherForecastUseCase(testCityName)

        // Then
        assertTrue(result is Result.Loading)
        coVerify { weatherRepository.getWeatherForecast(testCityName) }
    }

    @Test
    fun `getWeatherForecastUseCase handles empty city name`() = runTest {
        // Given
        val emptyCityName = ""
        val exception = Exception("City name cannot be empty")
        coEvery { weatherRepository.getWeatherForecast(emptyCityName) } returns Result.Error(exception)

        // When
        val result = getWeatherForecastUseCase(emptyCityName)

        // Then
        assertTrue(result is Result.Error)
        coVerify { weatherRepository.getWeatherForecast(emptyCityName) }
    }

    @Test
    fun `getWeatherForecastUseCase handles city not found`() = runTest {
        // Given
        val nonExistentCity = "NonExistentCity"
        val exception = Exception("City not found")
        coEvery { weatherRepository.getWeatherForecast(nonExistentCity) } returns Result.Error(exception)

        // When
        val result = getWeatherForecastUseCase(nonExistentCity)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("City not found", (result as Result.Error).exception.message)
        coVerify { weatherRepository.getWeatherForecast(nonExistentCity) }
    }

    // GetWeatherForecastByCoordinatesUseCase Tests
    @Test
    fun `getWeatherForecastByCoordinatesUseCase returns success when repository succeeds`() = runTest {
        // Given
        coEvery {
            weatherRepository.getWeatherForecastByCoordinates(testLatitude, testLongitude)
        } returns Result.Success(testWeatherForecast)

        // When
        val result = getWeatherForecastByCoordinatesUseCase(testLatitude, testLongitude)

        // Then
        assertTrue(result is Result.Success)
        val forecast = (result as Result.Success).data
        assertEquals(testWeatherForecast, forecast)
        assertEquals("Taipei", forecast.city)
        assertEquals(3, forecast.dailyForecasts.size)
        coVerify { weatherRepository.getWeatherForecastByCoordinates(testLatitude, testLongitude) }
    }

    @Test
    fun `getWeatherForecastByCoordinatesUseCase returns error when repository fails`() = runTest {
        // Given
        val exception = Exception("Invalid coordinates")
        coEvery {
            weatherRepository.getWeatherForecastByCoordinates(testLatitude, testLongitude)
        } returns Result.Error(exception)

        // When
        val result = getWeatherForecastByCoordinatesUseCase(testLatitude, testLongitude)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
        coVerify { weatherRepository.getWeatherForecastByCoordinates(testLatitude, testLongitude) }
    }

    @Test
    fun `getWeatherForecastByCoordinatesUseCase handles boundary coordinates`() = runTest {
        // Given - Test boundary coordinates (South Pole)
        val boundaryLatitude = -90.0
        val boundaryLongitude = -180.0
        val polarForecast = testWeatherForecast.copy(city = "South Pole")
        coEvery {
            weatherRepository.getWeatherForecastByCoordinates(boundaryLatitude, boundaryLongitude)
        } returns Result.Success(polarForecast)

        // When
        val result = getWeatherForecastByCoordinatesUseCase(boundaryLatitude, boundaryLongitude)

        // Then
        assertTrue(result is Result.Success)
        assertEquals("South Pole", (result as Result.Success).data.city)
        coVerify { weatherRepository.getWeatherForecastByCoordinates(boundaryLatitude, boundaryLongitude) }
    }

    // SearchCitiesUseCase Tests
    @Test
    fun `searchCitiesUseCase returns success with city results when repository succeeds`() = runTest {
        // Given
        coEvery { citySearchRepository.searchCities(testSearchQuery) } returns Result.Success(testCitySearchResults)

        // When
        val result = searchCitiesUseCase(testSearchQuery)

        // Then
        assertTrue(result is Result.Success)
        val cities = (result as Result.Success).data
        assertEquals(testCitySearchResults, cities)
        assertEquals(2, cities.size)
        assertEquals("Taipei", cities[0].name)
        assertEquals("TW", cities[0].country)
        assertEquals(25.0330, cities[0].lat ?: 0.0, 0.0001)
        assertEquals("Taichung", cities[1].name)
        coVerify { citySearchRepository.searchCities(testSearchQuery) }
    }

    @Test
    fun `searchCitiesUseCase returns empty list when no cities found`() = runTest {
        // Given
        coEvery { citySearchRepository.searchCities("XYZ123") } returns Result.Success(emptyList())

        // When
        val result = searchCitiesUseCase("XYZ123")

        // Then
        assertTrue(result is Result.Success)
        val cities = (result as Result.Success).data
        assertTrue(cities.isEmpty())
        coVerify { citySearchRepository.searchCities("XYZ123") }
    }

    @Test
    fun `searchCitiesUseCase returns error when repository fails`() = runTest {
        // Given
        val exception = Exception("API rate limit exceeded")
        coEvery { citySearchRepository.searchCities(testSearchQuery) } returns Result.Error(exception)

        // When
        val result = searchCitiesUseCase(testSearchQuery)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception.message, (result as Result.Error).exception.message)
        coVerify { citySearchRepository.searchCities(testSearchQuery) }
    }

    @Test
    fun `searchCitiesUseCase returns loading when repository is loading`() = runTest {
        // Given
        coEvery { citySearchRepository.searchCities(testSearchQuery) } returns Result.Loading

        // When
        val result = searchCitiesUseCase(testSearchQuery)

        // Then
        assertTrue(result is Result.Loading)
        coVerify { citySearchRepository.searchCities(testSearchQuery) }
    }

    @Test
    fun `searchCitiesUseCase handles empty search query`() = runTest {
        // Given
        val emptyQuery = ""
        val exception = Exception("Search query cannot be empty")
        coEvery { citySearchRepository.searchCities(emptyQuery) } returns Result.Error(exception)

        // When
        val result = searchCitiesUseCase(emptyQuery)

        // Then
        assertTrue(result is Result.Error)
        coVerify { citySearchRepository.searchCities(emptyQuery) }
    }

    @Test
    fun `searchCitiesUseCase handles very short search query`() = runTest {
        // Given
        val shortQuery = "a"
        val exception = Exception("Search query too short")
        coEvery { citySearchRepository.searchCities(shortQuery) } returns Result.Error(exception)

        // When
        val result = searchCitiesUseCase(shortQuery)

        // Then
        assertTrue(result is Result.Error)
        coVerify { citySearchRepository.searchCities(shortQuery) }
    }

    @Test
    fun `searchCitiesUseCase handles special characters in search query`() = runTest {
        // Given
        val specialCharQuery = "São"
        val specialCharResults = listOf(
            CitySearchResult("São Paulo", "BR", "SP", -23.5505, -46.6333)
        )
        coEvery { citySearchRepository.searchCities(specialCharQuery) } returns Result.Success(specialCharResults)

        // When
        val result = searchCitiesUseCase(specialCharQuery)

        // Then
        assertTrue(result is Result.Success)
        val cities = (result as Result.Success).data
        assertEquals(1, cities.size)
        assertEquals("São Paulo", cities[0].name)
        assertEquals("BR", cities[0].country)
        coVerify { citySearchRepository.searchCities(specialCharQuery) }
    }

    // Edge cases and validation tests
    @Test
    fun `forecast use cases handle extreme weather conditions`() = runTest {
        // Given
        val extremeCurrentWeather = testCurrentWeather.copy(
            city = "Antarctica",
            temperature = -45.0,
            description = "Extreme cold"
        )
        val extremeWeatherForecast = WeatherForecast(
            city = "Antarctica",
            country = "AQ",
            currentWeather = extremeCurrentWeather,
            dailyForecasts = listOf(
                DailyWeather(
                    date = "2024-01-15",
                    dayOfWeek = "Monday",
                    maxTemperature = -40.0,
                    minTemperature = -60.0,
                    description = "Blizzard",
                    humidity = 95,
                    iconCode = "13d",
                    chanceOfRain = 100
                )
            )
        )
        coEvery { weatherRepository.getWeatherForecast("Antarctica") } returns Result.Success(extremeWeatherForecast)

        // When
        val result = getWeatherForecastUseCase("Antarctica")

        // Then
        assertTrue(result is Result.Success)
        val forecast = (result as Result.Success).data
        assertEquals(-40.0, forecast.dailyForecasts[0].maxTemperature, 0.01)
        assertEquals(100, forecast.dailyForecasts[0].chanceOfRain)
        coVerify { weatherRepository.getWeatherForecast("Antarctica") }
    }

    @Test
    fun `forecast use cases handle network timeout scenarios`() = runTest {
        // Given
        val timeoutException = Exception("Request timeout")
        coEvery { weatherRepository.getWeatherForecast(testCityName) } returns Result.Error(timeoutException)

        // When
        val result = getWeatherForecastUseCase(testCityName)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Request timeout", (result as Result.Error).exception.message)
        coVerify { weatherRepository.getWeatherForecast(testCityName) }
    }

    @Test
    fun `searchCitiesUseCase handles large result sets`() = runTest {
        // Given
        val largeResultSet = (1..100).map { index ->
            CitySearchResult(
                name = "City$index",
                country = "US",
                state = "CA",
                lat = 37.0 + index * 0.01,
                lon = -122.0 + index * 0.01
            )
        }
        coEvery { citySearchRepository.searchCities("City") } returns Result.Success(largeResultSet)

        // When
        val result = searchCitiesUseCase("City")

        // Then
        assertTrue(result is Result.Success)
        val cities = (result as Result.Success).data
        assertEquals(100, cities.size)
        assertEquals("City1", cities[0].name)
        assertEquals("City100", cities[99].name)
        coVerify { citySearchRepository.searchCities("City") }
    }
}
