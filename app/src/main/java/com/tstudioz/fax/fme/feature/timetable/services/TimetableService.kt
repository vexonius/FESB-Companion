package com.tstudioz.fax.fme.feature.timetable.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.timetable.services.interfaces.TimetableServiceInterface
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class TimetableService(private val client: OkHttpClient) : TimetableServiceInterface {

    override suspend fun fetchTimeTable(url: String): NetworkServiceResult.TimeTableResult {
        val request = Request.Builder()
                .url(url)
                .build()

        val response: Response = client.newCall(request).execute()
        val value = response.body?.string()

        if (!response.isSuccessful || value.isNullOrEmpty()) {
            return NetworkServiceResult.TimeTableResult.Failure(Throwable("Failed to fetch schedule"))
        }

        return NetworkServiceResult.TimeTableResult.Success(value)
    }
}
