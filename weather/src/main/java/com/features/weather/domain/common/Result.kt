package com.features.weather.domain.common

/**
 * Sealed class representing different states of an operation
 * Used for proper error handling throughout the app
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Extension function to check if result is successful
 */
inline val <T> Result<T>.isSuccess: Boolean
    get() = this is Result.Success

/**
 * Extension function to check if result is error
 */
inline val <T> Result<T>.isError: Boolean
    get() = this is Result.Error

/**
 * Extension function to check if result is loading
 */
inline val <T> Result<T>.isLoading: Boolean
    get() = this is Result.Loading

/**
 * Extension function to get data safely from Result
 */
fun <T> Result<T>.getDataOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        else -> null
    }
}

/**
 * Extension function to get error safely from Result
 */
fun <T> Result<T>.getErrorOrNull(): Throwable? {
    return when (this) {
        is Result.Error -> exception
        else -> null
    }
}
