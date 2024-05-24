package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.attendance.services.AttendanceServiceInterface
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDaoInterface
import com.tstudioz.fax.fme.models.data.User

class AttendanceRepository(
    private val attendanceService: AttendanceServiceInterface,
    private val attendanceDao: AttendanceDaoInterface
): AttendanceRepositoryInterface {

    override suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult = attendanceService.fetchAttendance(user)

    override suspend fun insertAttendance(attendance: List<Dolazak>) {
        attendanceDao.insert(attendance)
    }

}
