package com.tstudioz.fax.fme.feature.weather

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class WeatherNetworkService(private val client: OkHttpClient) : WeatherNetworkInterface {

    override suspend fun fetchWeatherDetails(url: String): NetworkServiceResult.WeatherResult {
        val request: Request = Request.Builder()
            .url(url).header("Accept", "application/xml")
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