package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.common.user.models.User

interface AttendanceRepositoryInterface {

    suspend fun fetchAttendance(): NetworkServiceResult.AttendanceParseResult

    suspend fun insertAttendance(attendance: List<AttendanceEntry>)

    suspend fun readAttendance(): List<List<AttendanceEntry>>

}
