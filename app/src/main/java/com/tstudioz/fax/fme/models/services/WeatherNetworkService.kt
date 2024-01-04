package com.tstudioz.fax.fme.models.services

import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.interfaces.WeatherNetworkInterface
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.koin.java.KoinJavaComponent

class WeatherNetworkService : WeatherNetworkInterface {

    private val client: OkHttpClient by KoinJavaComponent.inject(OkHttpClient::class.java)

    override fun fetchWeatherDetails(url: String): Result.WeatherResult {
        val request: Request = Request.Builder()
            .url(url).header("Accept", "application/xml")
            .header("User-Agent", "FesbCompanion/1.0")
            .build()

        val response: Response = client.newCall(request).execute()

        return if(response.isSuccessful && response.body != null && response.code < 400) {
            Result.WeatherResult.Success(response.body!!.string())
        } else Result.WeatherResult.Failure(Throwable("Failed to fetch weather"))
    }

    override fun getWeekWeatherDetails() : Result.WeatherResult {
        TODO("Not yet implemented")
    }
}