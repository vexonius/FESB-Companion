package com.tstudioz.fax.fme.models.util

/**
 * Created by etino7 on 22.3.2020..
 */
sealed class WeatherResult<out T: Any> {
    data class Success<out T: Any>(val body: String): WeatherResult<T>()
    class Failure(exception: Throwable) : WeatherResult<Nothing>()
}