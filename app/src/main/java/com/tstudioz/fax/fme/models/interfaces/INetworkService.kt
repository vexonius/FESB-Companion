package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.WeatherResult
import com.tstudioz.fax.fme.weather.Current


interface INetworkService {
    fun getDayWeatherDetails() : WeatherResult<Current>
    fun getWeekWeatherDetails() : WeatherResult<List<Current>>
}