package com.tstudioz.fax.fme.feature.attendance.repository

import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User

interface AttendanceRepositoryInterface {

    suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult
    suspend fun insertAttendance(attendance: List<Dolazak>)
    suspend fun readAttendance(): List<List<Dolazak>>

}
