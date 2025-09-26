package com.features.city.presentation.state

import com.features.city.domain.model.City

/**
 * UI Events for City Selection Screen
 */
sealed class CitySelectionUiEvent {
    data class SearchCities(val query: String) : CitySelectionUiEvent()
    data class SelectCity(val city: City) : CitySelectionUiEvent()
    data class AddCity(val city: City) : CitySelectionUiEvent()
    data class RemoveCity(val cityName: String) : CitySelectionUiEvent()
    object ClearSearchResults : CitySelectionUiEvent()
    object ClearError : CitySelectionUiEvent()
}
