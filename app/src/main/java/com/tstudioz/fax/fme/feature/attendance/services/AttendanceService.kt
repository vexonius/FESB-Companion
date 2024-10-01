package com.tstudioz.fax.fme.feature.attendance.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.OkHttpClient
import okhttp3.Request

class AttendanceService(
    private val client: OkHttpClient,
) : AttendanceServiceInterface {

    override suspend fun fetchAllAttendance(): NetworkServiceResult.AttendanceFetchResult {
        val request: Request = Request.Builder()
            .url("https://raspored.fesb.unist.hr/part/prisutnost/opcenito/tablica")
            .get()
            .build()
        val response = client.newCall(request).execute()

        return if (response.isSuccessful) {
            NetworkServiceResult.AttendanceFetchResult.Success(response.body?.string() ?: "")
        } else {
            NetworkServiceResult.AttendanceFetchResult.Failure(Throwable("Failed to fetch attendance"))
        }
    }

    override suspend fun fetchAttendance(classId: String): NetworkServiceResult.AttendanceFetchResult {
        val request: Request = Request.Builder()
            .url("https://raspored.fesb.unist.hr${classId}")
            .get()
            .build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) { return NetworkServiceResult.AttendanceFetchResult.Failure(Throwable("Failed to fetch attendance details")) }

        return NetworkServiceResult.AttendanceFetchResult.Success(response.body?.string() ?: "")
    }
}