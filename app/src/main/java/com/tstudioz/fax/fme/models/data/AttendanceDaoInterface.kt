package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Dolazak

interface AttendanceDaoInterface {

    suspend fun insert(attendance: List<Dolazak>)

}