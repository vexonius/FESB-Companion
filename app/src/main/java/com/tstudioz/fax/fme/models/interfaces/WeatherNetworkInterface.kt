package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.Result

interface WeatherNetworkInterface {
    suspend fun fetchWeatherDetails(url: String) : Result.WeatherResult
    suspend fun getWeekWeatherDetails() : Result.WeatherResult

}