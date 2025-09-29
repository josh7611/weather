package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.features.weather.presentation.ui.WeatherApp
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity with Hilt support
 * Entry point for the weather forecast application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WeatherApp(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
