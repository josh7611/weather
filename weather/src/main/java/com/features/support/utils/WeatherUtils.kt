package com.features.support.utils

object WeatherUtils {

    /**
     * Maps OpenWeatherMap weather condition IDs to emoji icons
     */
    fun getWeatherEmoji(weatherId: Int): String {
        return when (weatherId) {
            // Thunderstorm
            in 200..232 -> "⛈️"
            // Drizzle
            in 300..321 -> "🌦️"
            // Rain
            in 500..504 -> "🌧️"
            511 -> "🌨️" // Freezing rain
            in 520..531 -> "🌦️"
            // Snow
            in 600..622 -> "❄️"
            // Atmosphere
            701 -> "🌫️" // Mist
            711 -> "💨" // Smoke
            721 -> "🌫️" // Haze
            731 -> "💨" // Dust
            741 -> "🌫️" // Fog
            751 -> "💨" // Sand
            761 -> "💨" // Dust
            762 -> "🌋" // Ash
            771 -> "💨" // Squall
            781 -> "🌪️" // Tornado
            // Clear
            800 -> "☀️"
            // Clouds
            801 -> "🌤️" // Few clouds
            802 -> "⛅" // Scattered clouds
            803 -> "🌥️" // Broken clouds
            804 -> "☁️" // Overcast
            else -> "🌡️" // Default
        }
    }

    /**
     * Converts temperature from Celsius to Fahrenheit
     */
    fun celsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9/5 + 32
    }

    /**
     * Converts wind speed from m/s to km/h
     */
    fun msToKmh(speedMs: Double): Double {
        return speedMs * 3.6
    }

    /**
     * Converts wind speed from m/s to mph
     */
    fun msToMph(speedMs: Double): Double {
        return speedMs * 2.237
    }

    /**
     * Gets wind direction from degrees
     */
    fun getWindDirection(degrees: Int): String {
        return when (degrees) {
            in 0..11, in 349..360 -> "N"
            in 12..33 -> "NNE"
            in 34..56 -> "NE"
            in 57..78 -> "ENE"
            in 79..101 -> "E"
            in 102..123 -> "ESE"
            in 124..146 -> "SE"
            in 147..168 -> "SSE"
            in 169..191 -> "S"
            in 192..213 -> "SSW"
            in 214..236 -> "SW"
            in 237..258 -> "WSW"
            in 259..281 -> "W"
            in 282..303 -> "WNW"
            in 304..326 -> "NW"
            in 327..348 -> "NNW"
            else -> "N/A"
        }
    }
}
