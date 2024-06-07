package com.tstudioz.fax.fme.feature.weather

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface WeatherNetworkInterface {

    suspend fun fetchWeatherDetails(url: String) : NetworkServiceResult.WeatherResult
}