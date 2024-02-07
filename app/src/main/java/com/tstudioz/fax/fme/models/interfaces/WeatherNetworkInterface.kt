package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface WeatherNetworkInterface {

    suspend fun fetchWeatherDetails(url: String) : NetworkServiceResult.WeatherResult
    suspend fun getWeekWeatherDetails() : NetworkServiceResult.WeatherResult

}