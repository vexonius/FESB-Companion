package com.tstudioz.fax.fme.models.services

import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.interfaces.TimetableServiceInterface
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class TimetableService(private val client: OkHttpClient) : TimetableServiceInterface {

    override suspend fun fetchTimeTable(userName: String, startDate: String, endDate: String): Result.TimeTableResult {
        val requestUrl = "https://raspored.fesb.unist.hr/part/raspored/kalendar?DataType=User&DataId=$userName&MinDate=$startDate&MaxDate=$endDate"

        val request = Request.Builder()
                .url(requestUrl)
                .build()

        val response: Response = client.newCall(request).execute()
        val value = response.body?.string()
        
        if (!response.isSuccessful || value.isNullOrEmpty()) {
            return Result.TimeTableResult.Failure(Throwable("Failed to fetch weather"))
        }

        return Result.TimeTableResult.Success(value)
    }

}