package com.tstudioz.fax.fme.feature.timetable.services

import com.tstudioz.fax.fme.feature.timetable.services.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.networking.clients.FESBPortalClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class TimetableService(private val client: OkHttpClient) : TimetableServiceInterface {

    private val baseURL = "https://raspored.fesb.unist.hr"

    override suspend fun fetchTimeTable(params: HashMap<String, String>): NetworkServiceResult.TimeTableResult {
        val endpointUrl  = "$baseURL/part/raspored/kalendar"
        val urlBuilder = endpointUrl
            .toHttpUrl()
            .newBuilder()

        for ((key, value) in params) {
            urlBuilder.addQueryParameter(key, value)
        }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        val response: Response = client.newCall(request).execute()
        val value = response.body?.string()

        if (!response.isSuccessful || value.isNullOrEmpty()) {
            return NetworkServiceResult.TimeTableResult.Failure(Throwable("Failed to fetch schedule"))
        }

        return NetworkServiceResult.TimeTableResult.Success(value)
    }

    override suspend fun fetchTimetableCalendar(params: HashMap<String, String>): NetworkServiceResult.TimeTableResult {
        val endpointUrl = "$baseURL/raspored/periodi-u-mjesecu-json"
        val urlBuilder = endpointUrl
            .toHttpUrl()
            .newBuilder()

        for ((key, value) in params) {
            urlBuilder.addQueryParameter(key, value)
        }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        val response: Response = client.newCall(request).execute()
        val value = response.body?.string()

        if (!response.isSuccessful || value.isNullOrEmpty()) {
            return NetworkServiceResult.TimeTableResult.Failure(Throwable("Failed to fetch schedule"))
        }

        return NetworkServiceResult.TimeTableResult.Success(value)
    }

}
