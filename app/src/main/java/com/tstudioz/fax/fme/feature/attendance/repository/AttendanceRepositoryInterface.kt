package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.database.models.User

interface AttendanceRepositoryInterface {

    suspend fun fetchAttendance(user: User): NetworkServiceResult.AttendanceParseResult
    suspend fun insertAttendance(attendance: List<AttendanceEntry>)
    suspend fun readAttendance(): List<List<AttendanceEntry>>

}
