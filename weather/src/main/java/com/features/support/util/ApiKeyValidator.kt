package com.features.support.util

import com.features.weather.BuildConfig

/**
 * Utility class to validate API key configuration
 * Follows Clean Architecture principles for configuration validation
 */
object ApiKeyValidator {

    private const val DEFAULT_API_KEY = "DEFAULT_API_KEY"
    private const val MIN_API_KEY_LENGTH = 10

    /**
     * Checks if the Weather API key is properly configured
     * Returns true if the key is valid, false if it's missing or using default value
     */
    fun isApiKeyValid(): Boolean {
        val apiKey = BuildConfig.WEATHER_API_KEY
        return apiKey.isNotBlank() &&
               apiKey != DEFAULT_API_KEY &&
               apiKey.length > MIN_API_KEY_LENGTH // OpenWeather API keys are typically 32 characters
    }
}
