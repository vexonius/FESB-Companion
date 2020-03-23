package com.tstudioz.fax.fme.networking

import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.weather.Current


interface INetworkService {
    fun getDayWeatherDetails() : Result<Current>
    fun getWeekWeatherDetails() : Result<List<Current>>
}