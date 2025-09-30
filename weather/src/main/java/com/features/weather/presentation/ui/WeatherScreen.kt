package com.features.weather.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.features.weather.domain.model.WeatherData
import com.features.weather.domain.model.DailyWeather
import com.features.weather.presentation.state.WeatherUiState
import com.features.weather.presentation.state.WeatherUiEvent
import com.features.weather.presentation.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

/**
 * Main weather screen following Clean Architecture patterns
 * Implements unidirectional data flow and proper state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    onNavigateToCitySelection: () -> Unit = {},
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WeatherScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToCitySelection = onNavigateToCitySelection,
        modifier = modifier
    )
}

/**
 * Weather screen content composable with proper state hoisting
 */
@Composable
private fun WeatherScreenContent(
    uiState: WeatherUiState,
    onEvent: (WeatherUiEvent) -> Unit,
    onNavigateToCitySelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle error display with snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Long
            )
            // Clear error after showing snackbar
            onEvent(WeatherUiEvent.ClearError)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        actionColor = MaterialTheme.colorScheme.error
                    )
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WeatherHeader(
                        selectedCity = uiState.selectedCity,
                        onCityClick = onNavigateToCitySelection,
                        onRefresh = { onEvent(WeatherUiEvent.RefreshWeather) }
                    )
                }

                item {
                    if (uiState.currentWeather != null) {
                        CurrentWeatherCard(weather = uiState.currentWeather)
                    }
                }

                item {
                    if (uiState.weeklyForecast.isNotEmpty()) {
                        WeeklyForecastSection(forecast = uiState.weeklyForecast)
                    }
                }
            }

            // Loading indicator
            if (uiState.isLoading || uiState.isRefreshing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Weather header with city selection and refresh functionality
 */
@Composable
private fun WeatherHeader(
    selectedCity: String,
    onCityClick: () -> Unit,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onCityClick) {
                    Text(
                        text = selectedCity.ifEmpty { "Search City" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Current weather information card
 */
@Composable
private fun CurrentWeatherCard(weather: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Temperature
            Text(
                text = "${weather.temperature.roundToInt()}°",
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Description
            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weather details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    label = "Feels like",
                    value = "${weather.feelsLike.roundToInt()}°"
                )
                WeatherDetailItem(
                    label = "Humidity",
                    value = "${weather.humidity}%"
                )
                WeatherDetailItem(
                    label = "Wind",
                    value = "${weather.windSpeed.roundToInt()} km/h"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    label = "Pressure",
                    value = "${weather.pressure} hPa"
                )
                WeatherDetailItem(
                    label = "Min/Max",
                    value = "${weather.minTemperature.roundToInt()}°/${weather.maxTemperature.roundToInt()}°"
                )
                weather.visibility?.let {
                    WeatherDetailItem(
                        label = "Visibility",
                        value = "${it / 1000} km"
                    )
                }
            }
        }
    }
}

/**
 * Weather detail item component
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Weekly forecast section
 */
@Composable
private fun WeeklyForecastSection(forecast: List<DailyWeather>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Weekly Forecast",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            forecast.forEach { dailyWeather ->
                DailyForecastItem(dailyWeather = dailyWeather)
                if (dailyWeather != forecast.last()) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

/**
 * Daily forecast item component
 */
@Composable
private fun DailyForecastItem(dailyWeather: DailyWeather) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day of week
        Text(
            text = dailyWeather.dayOfWeek,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.3f)
        )

        // Description
        Text(
            text = dailyWeather.description.replaceFirstChar { it.uppercase() },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f),
            textAlign = TextAlign.Center
        )

        // Temperature range
        Row(
            modifier = Modifier.weight(0.3f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${dailyWeather.minTemperature.roundToInt()}°",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "/",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "${dailyWeather.maxTemperature.roundToInt()}°",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Preview functions for development and testing
 */
@Preview(showBackground = true)
@Composable
private fun WeatherScreenPreview() {
    MaterialTheme {
        WeatherScreenContent(
            uiState = WeatherUiState(
                currentWeather = WeatherData(
                    temperature = 22.0,
                    feelsLike = 24.0,
                    minTemperature = 18.0,
                    maxTemperature = 26.0,
                    humidity = 65,
                    pressure = 1013,
                    description = "partly cloudy",
                    iconCode = "02d",
                    city = "New York",
                    country = "US",
                    windSpeed = 15.0,
                    windDirection = 180,
                    visibility = 10000
                ),
                weeklyForecast = listOf(
                    DailyWeather(
                        date = "2024-01-20",
                        dayOfWeek = "Today",
                        maxTemperature = 26.0,
                        minTemperature = 18.0,
                        description = "sunny",
                        iconCode = "01d",
                        humidity = 60,
                        chanceOfRain = 10
                    )
                ),
                selectedCity = "New York"
            ),
            onEvent = {},
            onNavigateToCitySelection = {}
        )
    }
}
