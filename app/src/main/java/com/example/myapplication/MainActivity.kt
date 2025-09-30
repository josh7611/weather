package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherApp(
                        modifier = Modifier.padding(
                            start = innerPadding.calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
                            end = innerPadding.calculateEndPadding(layoutDirection = LayoutDirection.Ltr),
                            bottom = 0.dp,
                            top = 0.dp
                        )
                    )
                }
            }
        }
    }
}
