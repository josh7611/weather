package com.features.weather.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.features.weather.domain.model.City
import com.features.weather.domain.model.CitySearchResult
import com.features.weather.presentation.state.CitySelectionUiState
import com.features.weather.presentation.state.CitySelectionUiEvent
import com.features.weather.presentation.viewmodel.CitySelectionViewModel

/**
 * City selection screen following Clean Architecture patterns
 * Allows users to search for cities and manage their saved cities
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: CitySelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CitySelectionScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

/**
 * City selection screen content with proper state hoisting
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitySelectionScreenContent(
    uiState: CitySelectionUiState,
    onEvent: (CitySelectionUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Select City") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { query ->
                    onEvent(CitySelectionUiEvent.SearchCities(query))
                },
                onSearch = { keyboardController?.hide() },
                placeholder = "Search for cities...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on search state
            when {
                uiState.isSearching || uiState.isLoading -> {
                    LoadingSection()
                }
                uiState.searchQuery.isNotEmpty() && uiState.searchResults.isNotEmpty() -> {
                    SearchResultsSection(
                        searchResults = uiState.searchResults,
                        onCityAdd = { citySearchResult ->
                            // Convert CitySearchResult to City for the event with current timestamp
                            val city = City(
                                name = citySearchResult.name,
                                country = citySearchResult.country,
                                latitude = citySearchResult.lat ?: 0.0,
                                longitude = citySearchResult.lon ?: 0.0,
                                lastUsedTime = System.currentTimeMillis() // Set current time when adding
                            )
                            onEvent(CitySelectionUiEvent.AddCity(city))
                        }
                    )
                }
                uiState.searchQuery.isNotEmpty() && uiState.searchResults.isEmpty() && !uiState.isSearching -> {
                    EmptySearchSection()
                }
                else -> {
                    SavedCitiesSection(
                        savedCities = uiState.savedCities,
                        selectedCity = uiState.selectedCity,
                        onCitySelect = { city ->
                            onEvent(CitySelectionUiEvent.SelectCity(city))
                            onNavigateBack()
                        },
                        onCityRemove = { cityName ->
                            onEvent(CitySelectionUiEvent.RemoveCity(cityName))
                        }
                    )
                }
            }
        }
    }

    // Error handling
    uiState.error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Handle error display (could show snackbar)
        }
    }
}

/**
 * Custom search bar component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        singleLine = true,
        modifier = modifier
    )
}

/**
 * Loading section component
 */
@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Searching...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Search results section
 */
@Composable
private fun SearchResultsSection(
    searchResults: List<CitySearchResult>,
    onCityAdd: (CitySearchResult) -> Unit
) {
    Column {
        Text(
            text = "Search Results",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults) { city ->
                SearchResultItem(
                    city = city,
                    onAdd = { onCityAdd(city) }
                )
            }
        }
    }
}

/**
 * Search result item component
 */
@Composable
private fun SearchResultItem(
    city: CitySearchResult,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = city.country,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add city",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Empty search results section
 */
@Composable
private fun EmptySearchSection() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No cities found",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
    }
}

/**
 * Saved cities section
 */
@Composable
private fun SavedCitiesSection(
    savedCities: List<City>,
    selectedCity: City?,
    onCitySelect: (City) -> Unit,
    onCityRemove: (String) -> Unit
) {
    Column {
        Text(
            text = "Saved Cities",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (savedCities.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved cities. Search to add some!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(savedCities) { city ->
                    SavedCityItem(
                        city = city,
                        isSelected = selectedCity?.name == city.name,
                        onSelect = { onCitySelect(city) },
                        onRemove = { onCityRemove(city.name) }
                    )
                }
            }
        }
    }
}

/**
 * Saved city item component
 */
@Composable
private fun SavedCityItem(
    city: City,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = city.country,
                    fontSize = 14.sp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (isSelected) {
                Text(
                    text = "Selected",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove city",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Preview functions for development and testing
 */
@Preview(showBackground = true)
@Composable
private fun CitySelectionScreenPreview() {
    MaterialTheme {
        CitySelectionScreenContent(
            uiState = CitySelectionUiState(
                savedCities = listOf(
                    City("New York", "US", 40.7128, -74.0060, isSelected = true),
                    City("London", "GB", 51.5074, -0.1278),
                    City("Tokyo", "JP", 35.6762, 139.6503)
                ),
                selectedCity = City("New York", "US", 40.7128, -74.0060, isSelected = true),
                searchQuery = "",
                searchResults = emptyList()
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CitySelectionScreenSearchPreview() {
    MaterialTheme {
        CitySelectionScreenContent(
            uiState = CitySelectionUiState(
                savedCities = listOf(),
                searchQuery = "Paris",
                searchResults = listOf(
                    CitySearchResult("Paris", "FR", null, 48.8566, 2.3522),
                    CitySearchResult("Paris", "US", "Texas", 33.6617, -95.5555)
                )
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}
