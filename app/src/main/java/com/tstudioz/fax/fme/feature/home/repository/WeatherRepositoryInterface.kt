package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.feature.home.WeatherDisplay

interface WeatherRepositoryInterface {

    suspend fun fetchWeatherDetails(): WeatherDisplay?

}