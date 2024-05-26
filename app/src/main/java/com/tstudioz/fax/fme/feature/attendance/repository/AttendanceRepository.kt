package com.tstudioz.fax.fme.feature.attendance.repository

import android.util.Log
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
import com.tstudioz.fax.fme.feature.attendance.parseAt
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
        val attendanceMap: MutableMap<String, MutableList<Dolazak>> = mutableMapOf()

        attendanceService.loginAttendance(user)
        val listUlrs = when (val list = attendanceService.fetchAttendance(user)) {
            is NetworkServiceResult.PrisutnostResult.Success -> {
                parseAt(list.data as String)
            }

            is NetworkServiceResult.PrisutnostResult.Failure -> {
                return NetworkServiceResult.PrisutnostResult.Failure(
                    Throwable("Error while fetching attendance data")
                )
            }
        }


        listUlrs.map {
            CoroutineScope(Dispatchers.IO).launch {
                when (val kolegijData = attendanceService.getDetailedPrisutnost(it.first)) {
                    is NetworkServiceResult.PrisutnostResult.Success -> {
                        synchronized(attendanceMap) {
                            val attendOneKolegij = parseAttendance(it.first, kolegijData.data as String, it.second)
                            attendanceMap[attendOneKolegij.first ?: ""] = attendOneKolegij.second
                        }
                    }

                    is NetworkServiceResult.PrisutnostResult.Failure -> {
                        return@launch
                    }
                }
            }
        }.forEach { it.join() }

        return NetworkServiceResult.PrisutnostResult.Success(attendanceMap)
    }

    override suspend fun insertAttendance(attendance: List<Dolazak>) {
        attendanceDao.insert(attendance)
    }

}
