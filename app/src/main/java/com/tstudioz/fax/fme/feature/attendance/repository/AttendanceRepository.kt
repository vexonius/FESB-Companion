package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
import com.tstudioz.fax.fme.feature.attendance.parseAttendList
import com.tstudioz.fax.fme.feature.attendance.parseAttendance
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class AttendanceRepository(
    private val attendanceService: AttendanceServiceInterface,
    private val attendanceDao: AttendanceDaoInterface
) : AttendanceRepositoryInterface {

    override suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult {
        when (val result = attendanceService.loginAttendance(user)) {
            is NetworkServiceResult.PrisutnostResult.Success -> {}

            is NetworkServiceResult.PrisutnostResult.Failure -> {
                return result
            }
        }

        when (val list = attendanceService.fetchAttendance(user)) {
            is NetworkServiceResult.PrisutnostResult.Success -> {
                val attendanceList = mutableListOf<List<Dolazak>>()
                val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                    println("Caught exception $throwable in CoroutineExceptionHandler")
                }
                val coroutines = parseAttendList(list.data as String).map {
                    CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
                        when (val kolegijData = attendanceService.getDetailedPrisutnost(it.first)) {
                            is NetworkServiceResult.PrisutnostResult.Success -> {
                                attendanceList.add(parseAttendance(it.first, kolegijData.data as String, it.second))
                            }

                            is NetworkServiceResult.PrisutnostResult.Failure -> {
                                throw Exception("Error while fetching attendance data")
                            }
                        }
                    }
                }
                coroutines.joinAll()

                return if (coroutines.any { it.isCancelled }) {
                    NetworkServiceResult.PrisutnostResult.Failure(
                        Throwable("Error while fetching attendance data")
                    )
                } else {
                    NetworkServiceResult.PrisutnostResult.Success(
                        attendanceList.sortedBy { it[0].predmet }.sortedBy { it[0].semestar }
                    )
                }
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
