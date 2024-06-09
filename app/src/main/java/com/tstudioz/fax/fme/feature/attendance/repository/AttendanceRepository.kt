package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.feature.attendance.ParseAttendance
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
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

    override suspend fun fetchAttendance(user: User): NetworkServiceResult.AttendanceParseResult {
        when (val result = attendanceService.loginAttendance(user)) {
            is NetworkServiceResult.AttendanceFetchResult.Success -> {}

            is NetworkServiceResult.AttendanceFetchResult.Failure -> {
                return NetworkServiceResult.AttendanceParseResult.Failure(Throwable("Error while logging in"))
            }
        }

        when (val list = attendanceService.fetchAttendance(user)) {
            is NetworkServiceResult.AttendanceFetchResult.Success -> {
                val attendanceList = mutableListOf<List<Dolazak>>()
                val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                    println("Caught exception $throwable in CoroutineExceptionHandler")
                }
                val parseAtt = ParseAttendance()
                val coroutines = parseAtt.parseAttendList(list.data as String).map {
                    CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
                        when (val kolegijData = attendanceService.getDetailedPrisutnost(it.first)) {
                            is NetworkServiceResult.AttendanceFetchResult.Success -> {
                                attendanceList.add(parseAtt.parseAttendance(it.first, kolegijData.data as String, it.second))
                            }

                            is NetworkServiceResult.AttendanceFetchResult.Failure -> {
                                throw Exception("Error while fetching attendance data")
                            }
                        }
                    }
                }
                coroutines.joinAll()

                return if (coroutines.any { it.isCancelled }) {
                    NetworkServiceResult.AttendanceParseResult.Failure(
                        Throwable("Error while fetching attendance data")
                    )
                } else {
                    insertAttendance(attendanceList.flatten())
                    NetworkServiceResult.AttendanceParseResult.Success(
                        attendanceList.sortedBy { it.first().predmet }.sortedBy { it.first().semestar }
                    )
                }
            }

            is NetworkServiceResult.AttendanceFetchResult.Failure -> {
                return NetworkServiceResult.AttendanceParseResult.Failure(
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
