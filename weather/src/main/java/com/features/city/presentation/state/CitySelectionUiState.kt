package com.features.city.presentation.state

import com.features.city.domain.model.City
import com.features.city.domain.repository.CitySearchResult


/**
 * UI State for City Selection Screen
 */
data class CitySelectionUiState(
    val savedCities: List<City> = emptyList(),
    val searchResults: List<CitySearchResult> = emptyList(),
    val selectedCity: City? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)
