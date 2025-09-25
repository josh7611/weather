package com.example.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class with Hilt support
 * Entry point for dependency injection
 */
@HiltAndroidApp
class WeatherApplication : Application()
