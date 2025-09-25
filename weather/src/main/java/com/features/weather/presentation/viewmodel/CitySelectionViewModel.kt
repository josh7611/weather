package com.features.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.features.weather.domain.common.Result
import com.features.weather.domain.usecase.*
import com.features.weather.presentation.state.CitySelectionUiState
import com.features.weather.presentation.state.CitySelectionUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for City Selection Screen following Clean Architecture
 * Manages city search, selection, and saved cities functionality
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class CitySelectionViewModel @Inject constructor(
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val searchCitiesUseCase: SearchCitiesUseCase,
    private val addCityUseCase: AddCityUseCase,
    private val removeCityUseCase: RemoveCityUseCase,
    private val setSelectedCityUseCase: SetSelectedCityUseCase,
    private val getSelectedCityUseCase: GetSelectedCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitySelectionUiState())
    val uiState: StateFlow<CitySelectionUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        loadSavedCities()
        loadSelectedCity()
        setupSearchFlow()
    }

    /**
     * Handle UI events with proper error handling
     */
    fun onEvent(event: CitySelectionUiEvent) {
        when (event) {
            is CitySelectionUiEvent.SearchCities -> {
                searchQuery.value = event.query
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is CitySelectionUiEvent.SelectCity -> selectCity(event.city)
            is CitySelectionUiEvent.AddCity -> addCity(event.city)
            is CitySelectionUiEvent.RemoveCity -> removeCity(event.cityName)
            CitySelectionUiEvent.ClearSearchResults -> clearSearchResults()
            CitySelectionUiEvent.ClearError -> clearError()
        }
    }

    /**
     * Load saved cities from repository
     */
    private fun loadSavedCities() {
        viewModelScope.launch {
            getSavedCitiesUseCase().collect { cities ->
                _uiState.value = _uiState.value.copy(savedCities = cities)
            }
        }
    }

    /**
     * Load currently selected city
     */
    private fun loadSelectedCity() {
        viewModelScope.launch {
            when (val result = getSelectedCityUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(selectedCity = result.data)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.exception.message)
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Setup search flow with debouncing to avoid excessive API calls
     */
    private fun setupSearchFlow() {
        viewModelScope.launch {
            searchQuery
                .debounce(300) // Wait 300ms after user stops typing
                .filter { it.isNotBlank() && it.length >= 2 }
                .distinctUntilChanged()
                .collect { query ->
                    searchCities(query)
                }
        }
    }

    /**
     * Search for cities using the search use case
     */
    private fun searchCities(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, error = null)

            when (val result = searchCitiesUseCase(query)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        searchResults = result.data,
                        isSearching = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message,
                        isSearching = false
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isSearching = true)
                }
            }
        }
    }

    /**
     * Select a city and update weather data
     */
    private fun selectCity(city: com.features.weather.domain.model.City) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = setSelectedCityUseCase(city.name)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        selectedCity = city,
                        isLoading = false
                    )
                    // Note: Weather update will be handled by WeatherViewModel
                    // when it observes the selected city change
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message,
                        isLoading = false
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Add a city to saved cities list
     */
    private fun addCity(city: com.features.weather.domain.model.City) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = addCityUseCase(city)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    clearSearchResults()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message,
                        isLoading = false
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Remove a city from saved cities list
     */
    private fun removeCity(cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = removeCityUseCase(cityName)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message,
                        isLoading = false
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Clear search results and query
     */
    private fun clearSearchResults() {
        searchQuery.value = ""
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            searchQuery = ""
        )
    }

    /**
     * Clear error state
     */
    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
