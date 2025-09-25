package com.features.weather.domain.repository

/**
 * Represents a city search result for city selection/search
 */
data class CitySearchResult(
    val name: String,
    val country: String,
    val state: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
)

