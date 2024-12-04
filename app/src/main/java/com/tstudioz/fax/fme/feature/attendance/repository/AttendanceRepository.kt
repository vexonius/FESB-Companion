package com.tstudioz.fax.fme.feature.attendance.repository

import android.util.Log
import com.tstudioz.fax.fme.feature.attendance.models.AttendanceEntry
import com.tstudioz.fax.fme.feature.attendance.ParseAttendance
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class AttendanceRepository(
    private val attendanceService: AttendanceServiceInterface,
    private val attendanceDao: AttendanceDaoInterface,
    private val parseAttendance: ParseAttendance = ParseAttendance()
) : AttendanceRepositoryInterface {

    override suspend fun fetchAttendance(): NetworkServiceResult.AttendanceParseResult {
        when (val list = attendanceService.fetchAllAttendance()) {
            is NetworkServiceResult.AttendanceFetchResult.Success -> {
                val attendanceList: List<List<AttendanceEntry>> = runBlocking {
                    parseAttendance.parseAttendList(list.data).map {
                        async {
                            Log.d("AttendanceRepository", "Fetching attendance for ${it.first.text()}")
                            when (val classData = attendanceService.fetchAttendance(it.first.attr("href"))) {
                                is NetworkServiceResult.AttendanceFetchResult.Success -> {
                                    parseAttendance.parseAttendance(
                                        it.first,
                                        classData.data,
                                        it.second
                                    )
                                }

                                is NetworkServiceResult.AttendanceFetchResult.Failure -> {
                                    emptyList()
                                }
                            }
                        }
                    }
                }.awaitAll()

                return if (attendanceList.isEmpty()) {
                    NetworkServiceResult.AttendanceParseResult.Failure(
                        Throwable("Error while fetching attendance data")
                    )
                } else {
                    insertAttendance(attendanceList.flatten())
                    NetworkServiceResult.AttendanceParseResult.Success(
                        attendanceList
                            .sortedBy { it.first().`class` }
                            .sortedBy { it.first().semester }
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

    override suspend fun insertAttendance(attendance: List<AttendanceEntry>) {
        attendanceDao.insert(attendance)
    }

    override suspend fun readAttendance(): List<List<AttendanceEntry>> {
        return attendanceDao.read()
    }

}
