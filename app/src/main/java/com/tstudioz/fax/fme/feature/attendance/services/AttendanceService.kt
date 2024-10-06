package com.tstudioz.fax.fme.feature.attendance.services

import android.util.Log
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
        val success = response.isSuccessful
        val data = response.body?.string() ?: ""
        response.close()

        return if (success) {
            NetworkServiceResult.AttendanceFetchResult.Success(data)
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
        val success = response.isSuccessful
        val data = response.body?.string() ?: ""
        response.close()
        Log.d("AttendanceService", "Attendance data fetched$success")

        if (!success) { return NetworkServiceResult.AttendanceFetchResult.Failure(Throwable("Failed to fetch attendance details")) }

        return NetworkServiceResult.AttendanceFetchResult.Success(data)
    }
}