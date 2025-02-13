package com.tstudioz.fax.fme.feature.attendance.dao

import com.tstudioz.fax.fme.feature.attendance.models.AttendanceEntry

interface AttendanceDaoInterface {

    suspend fun insert(attendance: List<AttendanceEntry>)

    suspend fun read(): List<List<AttendanceEntry>>

}