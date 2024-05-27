package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
import com.tstudioz.fax.fme.feature.attendance.parseAttendList
import com.tstudioz.fax.fme.feature.attendance.parseAttendance
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttendanceRepository(
    private val attendanceService: AttendanceServiceInterface,
    private val attendanceDao: AttendanceDaoInterface
) : AttendanceRepositoryInterface {

    override suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult {
        attendanceService.loginAttendance(user)

        when (val list = attendanceService.fetchAttendance(user)) {
            is NetworkServiceResult.PrisutnostResult.Success -> {
                var error = false
                val attendanceList = mutableListOf<List<Dolazak>>()
                try {
                    parseAttendList(list.data as String).map {
                        CoroutineScope(Dispatchers.IO).launch {
                            when (val kolegijData = attendanceService.getDetailedPrisutnost(it.first)) {
                                is NetworkServiceResult.PrisutnostResult.Success -> {
                                    val attendOneKolegij =
                                        parseAttendance(it.first, kolegijData.data as String, it.second)
                                    attendanceList.add(attendOneKolegij)
                                }

                                is NetworkServiceResult.PrisutnostResult.Failure -> {
                                    error = true
                                    return@launch
                                }
                            }
                        }
                    }.forEach { it.join() }
                } catch (e: Throwable) {
                    error = true
                }
                if (error) {
                    return NetworkServiceResult.PrisutnostResult.Failure(
                        Throwable("Error while fetching attendance data")
                    )
                }
                return NetworkServiceResult.PrisutnostResult.Success(
                    attendanceList.sortedBy { it[0].predmet }
                    .sortedBy { it[0].semestar }
                )
            }

            is NetworkServiceResult.PrisutnostResult.Failure -> {
                return NetworkServiceResult.PrisutnostResult.Failure(
                    Throwable("Error while fetching attendance data")
                )
            }
        }


    }

    override suspend fun insertAttendance(attendance: List<Dolazak>) {
        attendanceDao.insert(attendance)
    }

    override suspend fun readAttendance(): List<List<Dolazak>> {
        return attendanceDao.read()
    }

}
