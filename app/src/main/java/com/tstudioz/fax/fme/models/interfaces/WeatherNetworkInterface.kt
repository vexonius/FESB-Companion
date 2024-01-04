package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.Result


interface WeatherNetworkInterface {
    fun fetchWeatherDetails(url: String) : Result.WeatherResult
    fun getWeekWeatherDetails() : Result.WeatherResult
}