package com.tstudioz.fax.fme.feature.home.repository

import android.util.Log
import com.tstudioz.fax.fme.feature.home.WeatherFeature
import com.tstudioz.fax.fme.feature.home.services.WeatherServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import kotlinx.serialization.json.Json

class WeatherRepository(private val weatherNetworkService: WeatherServiceInterface) : WeatherRepositoryInterface {

    override suspend fun fetchWeatherDetails(): WeatherFeature? {
        return when (val result = weatherNetworkService.fetchWeatherDetails()) {
            is NetworkServiceResult.WeatherResult.Success -> {
                val test = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
                test.decodeFromString<WeatherFeature>(result.data)
            }

            is NetworkServiceResult.WeatherResult.Failure -> {
                Log.e(this.javaClass.canonicalName, "Timetable fetching error")
                null
            }
        }
    }
}