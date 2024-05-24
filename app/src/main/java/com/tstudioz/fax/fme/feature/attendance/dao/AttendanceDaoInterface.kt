package com.tstudioz.fax.fme.feature.attendance.dao

import com.tstudioz.fax.fme.database.models.Dolazak

interface AttendanceDaoInterface {

    suspend fun insert(attendance: List<Dolazak>)

}