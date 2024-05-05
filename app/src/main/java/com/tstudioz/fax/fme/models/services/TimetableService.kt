package com.tstudioz.fax.fme.models.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.interfaces.TimetableServiceInterface
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TimetableService(private val client: OkHttpClient) : TimetableServiceInterface {

    override suspend fun fetchTimeTable(userName: String, startDate: LocalDate, endDate: LocalDate): NetworkServiceResult.TimeTableResult {

        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")

        val requestUrl = "https://raspored.fesb.unist.hr/part/raspored/kalendar?" +
                "DataType=User&DataId=$userName" +
                "&MinDate=${dateFormatter.format(startDate)}" +
                "&MaxDate=${dateFormatter.format(endDate)}"

        val request = Request.Builder()
                .url(requestUrl)
                .build()

        val response: Response = client.newCall(request).execute()
        val value = response.body?.string()
        
        if (!response.isSuccessful || value.isNullOrEmpty()) {
            return NetworkServiceResult.TimeTableResult.Failure(Throwable("Failed to fetch schedule"))
        }

        return NetworkServiceResult.TimeTableResult.Success(value)
    }

    override suspend fun fetchTimeTableInfo(startDate: String, endDate: String): NetworkServiceResult.TimeTableResult {
        val requestUrl = "https://raspored.fesb.unist.hr/raspored/periodi-u-mjesecu-json?FromDate=$startDate&ToDate=$endDate"

        val request = Request.Builder()
            .url(requestUrl)
            .build()

        val response: Response = client.newCall(request).execute()
        val value = response.body?.string()

        if (!response.isSuccessful || value.isNullOrEmpty()) {
            return NetworkServiceResult.TimeTableResult.Failure(Throwable("Failed to fetch schedule"))
        }

        return NetworkServiceResult.TimeTableResult.Success(value)
    }

}
