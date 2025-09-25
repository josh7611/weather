package com.features.support.di

import com.features.weather.BuildConfig.WEATHER_API_KEY
import com.features.weather.data.repository.CityRepositoryImpl
import com.features.weather.data.repository.WeatherRepositoryImpl
import com.features.weather.data.repository.OpenWeatherCityRepository
import com.features.weather.domain.repository.CityRepository
import com.features.weather.domain.repository.WeatherRepository
import com.features.weather.domain.repository.CitySearchRepository
import com.features.weather.data.network.WeatherApiService
import com.features.weather.data.network.GeocodingApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for network dependencies
 * Following Clean Architecture dependency injection patterns
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val GEOCODING_BASE_URL = "https://api.openweathermap.org/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    @WeatherRetrofit
    fun provideWeatherRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @GeocodingRetrofit
    fun provideGeocodingRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(@WeatherRetrofit retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingApiService(@GeocodingRetrofit retrofit: Retrofit): GeocodingApiService {
        return retrofit.create(GeocodingApiService::class.java)
    }

    @Provides
    @Singleton
    @OpenWeatherApiKey
    fun provideOpenWeatherApiKey(): String {
        return WEATHER_API_KEY
    }
}

/**
 * Qualifier for Weather Retrofit instance
 */
@javax.inject.Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherRetrofit

/**
 * Qualifier for Geocoding Retrofit instance
 */
@javax.inject.Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocodingRetrofit

/**
 * Hilt module for repository implementations binding
 * Following Clean Architecture dependency injection patterns
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindCityRepository(
        cityRepositoryImpl: CityRepositoryImpl
    ): CityRepository

    @Binds
    @Singleton
    abstract fun bindCitySearchRepository(
        openWeatherCityRepository: OpenWeatherCityRepository
    ): CitySearchRepository
}
