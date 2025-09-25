package com.features.weather.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Main Weather App composable with Navigation Compose
 * Implements proper screen navigation following Clean Architecture
 */
@Composable
fun WeatherApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "weather",
        modifier = modifier
    ) {
        composable("weather") {
            WeatherScreen(
                onNavigateToCitySelection = {
                    navController.navigate("city_selection")
                }
            )
        }

        composable("city_selection") {
            CitySelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
