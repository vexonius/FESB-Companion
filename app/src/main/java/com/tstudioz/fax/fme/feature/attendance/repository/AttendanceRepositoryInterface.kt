package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.feature.attendance.models.AttendanceEntry
import com.tstudioz.fax.fme.models.NetworkServiceResult

interface AttendanceRepositoryInterface {

    suspend fun fetchAttendance(): NetworkServiceResult.AttendanceParseResult

    suspend fun insertAttendance(attendance: List<AttendanceEntry>)

    suspend fun readAttendance(): List<List<AttendanceEntry>>

}
