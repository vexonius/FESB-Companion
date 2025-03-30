package com.tstudioz.fax.fme.feature.attendance.models

import androidx.room.Entity

@Entity(tableName = "attendanceentryroom")
data class AttendanceEntry(
    @androidx.room.PrimaryKey
    var id: String = "",
    var subject: String = "",
    var type: String = "",
    var link: String = "",
    var attended: Int = 0,
    var absent: Int = 0,
    var required: Int = 0,
    var semester: Int = 0,
    var total: Int = 0,
)
