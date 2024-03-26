package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.models.NetworkServiceResult

interface AttendanceRepositoryInterface {

    suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult
    suspend fun insertAttendance(attendance: List<Dolazak>)

}
