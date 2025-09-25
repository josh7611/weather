package com.features.weather.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.features.weather.domain.model.DailyWeather
import com.features.weather.domain.model.WeatherData
import com.features.weather.presentation.state.WeatherUiEvent
import com.features.weather.presentation.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

/**
 * Main Weather App composable following Clean Architecture
 * Implements state hoisting and unidirectional data flow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCitySelection by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with city selector and refresh
        WeatherHeader(
            selectedCity = uiState.selectedCity,
            onCityClick = { showCitySelection = true },
            onRefreshClick = { viewModel.onEvent(WeatherUiEvent.RefreshWeather) },
            isLoading = uiState.isLoading
        )

        // Error handling
        uiState.error?.let { error ->
            ErrorCard(
                error = error,
                onDismiss = { viewModel.onEvent(WeatherUiEvent.ClearError) }
            )
        }

        // Weather content
        when {
            uiState.isLoading && uiState.currentWeather == null -> {
                LoadingScreen()
            }
            uiState.currentWeather != null -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Current weather card
                    item {
                        CurrentWeatherCard(
                            weatherData = uiState.currentWeather!!
                        )
                    }

                    // Weekly forecast
                    if (uiState.weeklyForecast.isNotEmpty()) {
                        item {
                            WeeklyForecastSection(
                                dailyForecasts = uiState.weeklyForecast
                            )
                        }
                    }
                }
            }
            else -> {
                EmptyStateCard()
            }
        }
    }

    // City selection modal bottom sheet
    if (showCitySelection) {
        CitySelectionBottomSheet(
            onDismiss = { showCitySelection = false },
            onCitySelected = { cityName: String ->
                viewModel.onEvent(WeatherUiEvent.SelectCity(cityName))
                showCitySelection = false
            }
        )
    }
}

/**
 * Header component with city selector and refresh button
 */
@Composable
private fun WeatherHeader(
    selectedCity: String,
    onCityClick: () -> Unit,
    onRefreshClick: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // City selector
        OutlinedButton(
            onClick = onCityClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Select city",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedCity.ifEmpty { "Select City" },
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Refresh button
        IconButton(
            onClick = onRefreshClick,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh weather"
                )
            }
        }
    }
}

/**
 * Current weather display card
 */
@Composable
private fun CurrentWeatherCard(
    weatherData: WeatherData
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // City and description
            Text(
                text = "${weatherData.city}, ${weatherData.country}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = weatherData.description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // Temperature section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${weatherData.temperature.roundToInt()}°C",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Feels like ${weatherData.feelsLike.roundToInt()}°C",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            // Weather details grid
            WeatherDetailsGrid(weatherData = weatherData)
        }
    }
}

/**
 * Weather details in a grid layout
 */
@Composable
private fun WeatherDetailsGrid(
    weatherData: WeatherData
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                label = "Min",
                value = "${weatherData.minTemperature.roundToInt()}°C"
            )
            WeatherDetailItem(
                label = "Max",
                value = "${weatherData.maxTemperature.roundToInt()}°C"
            )
            WeatherDetailItem(
                label = "Humidity",
                value = "${weatherData.humidity}%"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                label = "Pressure",
                value = "${weatherData.pressure} hPa"
            )
            WeatherDetailItem(
                label = "Wind",
                value = "${weatherData.windSpeed} m/s"
            )
            WeatherDetailItem(
                label = "Direction",
                value = "${weatherData.windDirection}°"
            )
        }
    }
}

/**
 * Individual weather detail item
 */
@Composable
private fun WeatherDetailItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Weekly forecast section with horizontal scroll
 */
@Composable
private fun WeeklyForecastSection(
    dailyForecasts: List<DailyWeather>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "7-Day Forecast",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dailyForecasts) { dailyWeather ->
                    DailyForecastItem(dailyWeather = dailyWeather)
                }
            }
        }
    }
}

/**
 * Individual daily forecast item
 */
@Composable
private fun DailyForecastItem(
    dailyWeather: DailyWeather
) {
    Card(
        modifier = Modifier.width(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = dailyWeather.dayOfWeek.take(3),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "${dailyWeather.maxTemperature.roundToInt()}°",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${dailyWeather.minTemperature.roundToInt()}°",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (dailyWeather.chanceOfRain > 0) {
                Text(
                    text = "${dailyWeather.chanceOfRain}%",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Loading screen component
 */
@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Loading weather data...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Empty state when no weather data is available
 */
@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "No Weather Data",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Please select a city to view weather information",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Error card with dismiss functionality
 */
@Composable
private fun ErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Dismiss")
            }
        }
    }
}

/**
 * City Selection Bottom Sheet Modal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitySelectionBottomSheet(
    onDismiss: () -> Unit,
    onCitySelected: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Select City",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sample cities - in a real app, this would come from a city search/selection component
            val sampleCities = listOf(
                "New York, US",
                "London, GB",
                "Tokyo, JP",
                "Sydney, AU",
                "Paris, FR",
                "Berlin, DE"
            )

            LazyColumn {
                items(sampleCities) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { onCitySelected(city) }
                    ) {
                        Text(
                            text = city,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
