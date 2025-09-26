package com.features.weather.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * Warning dialog for missing API key configuration
 * Follows Material Design 3 guidelines and Clean Architecture UI patterns
 */
@Composable
fun ApiKeyWarningDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = onDismiss
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "⚠️ API Key Required",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = """
                    The Weather API key is not configured properly.
                    
                    To fix this issue:
                    1. Open the 'environment.properties' file in the project root
                    2. Add your OpenWeather API key:
                       WEATHER_API_KEY=your_api_key_here
                    3. Get a free API key at: https://openweathermap.org/api
                    4. Rebuild the app
                    
                    The app may not work correctly without a valid API key.
                """.trimIndent(),
                textAlign = TextAlign.Start
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("I Understand")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue Anyway")
            }
        }
    )
}
