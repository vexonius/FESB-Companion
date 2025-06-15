package com.tstudioz.fax.fme.feature.attendance.services

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface AttendanceServiceInterface {

    suspend fun fetchAllAttendance(): NetworkServiceResult.AttendanceFetchResult

    suspend fun fetchAttendance(classId: String): NetworkServiceResult.AttendanceFetchResult

}
