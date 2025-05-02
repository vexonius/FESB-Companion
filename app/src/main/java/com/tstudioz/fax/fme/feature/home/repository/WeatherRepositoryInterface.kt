package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.feature.home.WeatherFeature

interface WeatherRepositoryInterface {

    suspend fun fetchWeatherDetails(): WeatherFeature?

}