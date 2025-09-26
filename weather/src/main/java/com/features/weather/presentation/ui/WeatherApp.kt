package com.features.weather.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.features.city.presentation.ui.CitySelectionScreen
import com.features.support.util.ApiKeyValidator
import com.features.weather.presentation.ui.components.ApiKeyWarningDialog

/**
 * Main Weather App composable with Navigation Compose
 * Implements proper screen navigation following Clean Architecture
 */
@Composable
fun WeatherApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // State for API key warning dialog
    var showApiKeyWarning by remember { mutableStateOf(false) }
    var hasCheckedApiKey by remember { mutableStateOf(false) }

    // Check API key configuration on first composition
    LaunchedEffect(Unit) {
        if (!hasCheckedApiKey && !ApiKeyValidator.isApiKeyValid()) {
            showApiKeyWarning = true
        }
        hasCheckedApiKey = true
    }

    // Show API key warning dialog if needed
    if (showApiKeyWarning) {
        ApiKeyWarningDialog(
            onDismiss = { showApiKeyWarning = false },
            onConfirm = { showApiKeyWarning = false }
        )
    }

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
