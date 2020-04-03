package com.tstudioz.fax.fme.models

import com.tstudioz.fax.fme.weather.Current

/**
 * Created by etino7 on 22.3.2020..
 */
sealed class WeatherResult<out T: Any> {
    data class Success<out T: Any>(val data: Current): WeatherResult<T>()
    class Failure(exception: Exception) : WeatherResult<Nothing>()
}