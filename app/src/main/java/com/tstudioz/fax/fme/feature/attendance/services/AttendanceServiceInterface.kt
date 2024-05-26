package com.tstudioz.fax.fme.feature.attendance.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import org.jsoup.nodes.Element

interface AttendanceServiceInterface {

     suspend fun loginAttendance(user: User): NetworkServiceResult.PrisutnostResult

     suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult

     suspend fun getDetailedPrisutnost(element: Element): NetworkServiceResult.PrisutnostResult

}
