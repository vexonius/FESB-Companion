package com.tstudioz.fax.fme.feature.attendance.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User

interface AttendanceServiceInterface {

     suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult

}
