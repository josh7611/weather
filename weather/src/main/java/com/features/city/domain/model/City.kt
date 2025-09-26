package com.features.city.domain.model

/**
 * Domain model for city information
 * Pure Kotlin class without Android dependencies
 */
data class City(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isSelected: Boolean = false,
    val lastUsedTime: Long = System.currentTimeMillis()
)
