package com.tstudioz.fax.fme.feature.attendance.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.common.user.models.User
import org.jsoup.nodes.Element

interface AttendanceServiceInterface {

     suspend fun fetchAllAttendance(): NetworkServiceResult.AttendanceFetchResult

     suspend fun fetchAttendance(classId: String): NetworkServiceResult.AttendanceFetchResult

}
