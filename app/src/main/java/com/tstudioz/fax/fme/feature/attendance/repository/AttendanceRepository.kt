package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.feature.attendance.ParseAttendance
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class AttendanceRepository(
    private val attendanceService: AttendanceServiceInterface,
    private val attendanceDao: AttendanceDaoInterface,
    private val parseAttendance: ParseAttendance = ParseAttendance()
) : AttendanceRepositoryInterface {

    override suspend fun fetchAttendance(): NetworkServiceResult.AttendanceParseResult {
        when (val list = attendanceService.fetchAllAttendance()) {
            is NetworkServiceResult.AttendanceFetchResult.Success -> {
                val attendanceList = mutableListOf<List<AttendanceEntry>>()

                val coroutines = parseAttendance.parseAttendList(list.data)
                    .map {
                        CoroutineScope(Dispatchers.IO).launch {
                            when (val classData = attendanceService.fetchAttendance(it.first.attr("href"))) {
                                is NetworkServiceResult.AttendanceFetchResult.Success -> {
                                    attendanceList.add(parseAttendance.parseAttendance(it.first, classData.data, it.second))
                                }

                                is NetworkServiceResult.AttendanceFetchResult.Failure -> {
                                    NetworkServiceResult.AttendanceParseResult.Failure(
                                         Throwable("Error while fetching attendance data")
                                    )
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
