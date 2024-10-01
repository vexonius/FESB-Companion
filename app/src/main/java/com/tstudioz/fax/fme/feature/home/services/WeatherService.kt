package com.tstudioz.fax.fme.feature.home.services

import com.tstudioz.fax.fme.feature.home.view.HomeFragment
import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class WeatherService(private val client: OkHttpClient) : WeatherServiceInterface {

    override suspend fun fetchWeatherDetails(): NetworkServiceResult.WeatherResult {
        val request: Request = Request.Builder()
            .url("https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=43.511287&lon=16.469252")
            .header("Accept", "application/xml")
            .header("User-Agent", "FesbCompanion/1.0")
            .build()

        val response: Response = client.newCall(request).execute()
        val value = response.body?.string()

        if (!response.isSuccessful || value.isNullOrEmpty()) {
            return NetworkServiceResult.WeatherResult.Failure(Throwable("Failed to fetch weather"))
        }

        return NetworkServiceResult.WeatherResult.Success(value)
    }
}