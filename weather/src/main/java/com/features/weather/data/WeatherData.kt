package com.features.weather.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for current weather API response
 * Maps directly to OpenWeatherMap API structure
 */
data class WeatherResponseDto(
    @SerializedName("coord")
    val coordinates: CoordinatesDto,
    @SerializedName("weather")
    val weather: List<WeatherDescriptionDto>,
    @SerializedName("main")
    val main: MainWeatherDto,
    @SerializedName("wind")
    val wind: WindDto,
    @SerializedName("visibility")
    val visibility: Int? = null,
    @SerializedName("sys")
    val sys: SysDto,
    @SerializedName("name")
    val name: String
)

/**
 * DTO for coordinates data
 */
data class CoordinatesDto(
    @SerializedName("lon")
    val longitude: Double,
    @SerializedName("lat")
    val latitude: Double
)

/**
 * DTO for weather description data
 */
data class WeatherDescriptionDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)

/**
 * DTO for main weather data (temperature, pressure, humidity)
 */
data class MainWeatherDto(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val minTemperature: Double,
    @SerializedName("temp_max")
    val maxTemperature: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int
)

/**
 * DTO for wind data
 */
data class WindDto(
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("deg")
    val direction: Int
)

/**
 * DTO for system data (country, sunrise, sunset)
 */
data class SysDto(
    @SerializedName("country")
    val country: String,
    @SerializedName("sunrise")
    val sunrise: Long? = null,
    @SerializedName("sunset")
    val sunset: Long? = null
)

/**
 * DTO for forecast API response
 */
data class ForecastResponseDto(
    @SerializedName("city")
    val city: CityDto,
    @SerializedName("list")
    val forecasts: List<ForecastItemDto>
)

/**
 * DTO for city information in forecast response
 */
data class CityDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("coord")
    val coordinates: CoordinatesDto
)

/**
 * DTO for individual forecast item
 */
data class ForecastItemDto(
    @SerializedName("dt")
    val datetime: Long,
    @SerializedName("main")
    val main: MainWeatherDto,
    @SerializedName("weather")
    val weather: List<WeatherDescriptionDto>,
    @SerializedName("dt_txt")
    val datetimeText: String,
    @SerializedName("pop")
    val chanceOfRain: Double? = null
)

/**
 * DTO for city search response
 */
data class CitySearchDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lon")
    val longitude: Double
)
