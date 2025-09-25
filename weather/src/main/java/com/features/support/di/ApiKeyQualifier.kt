package com.features.support.di

import javax.inject.Qualifier

/**
 * Qualifier annotation for OpenWeatherMap API key injection
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenWeatherApiKey
