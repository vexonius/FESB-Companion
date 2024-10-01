package com.tstudioz.fax.fme.feature.attendance.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.common.user.models.User
import org.jsoup.nodes.Element

interface AttendanceServiceInterface {

     suspend fun loginAttendance(user: User): NetworkServiceResult.AttendanceFetchResult

     suspend fun fetchAttendance(user: User): NetworkServiceResult.AttendanceFetchResult

     suspend fun getDetailedPrisutnost(element: Element): NetworkServiceResult.AttendanceFetchResult

}
