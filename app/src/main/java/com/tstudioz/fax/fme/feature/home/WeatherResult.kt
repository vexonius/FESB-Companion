package com.tstudioz.fax.fme.feature.home

sealed class WeatherResult<out T: Any> {
    data class Success<out T: Any>(val body: String): WeatherResult<T>()
    class Failure(exception: Throwable) : WeatherResult<Nothing>()
}