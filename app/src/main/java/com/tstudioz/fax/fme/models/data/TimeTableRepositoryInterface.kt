package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.database.models.TimeTableInfo

interface TimeTableRepositoryInterface {

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<TimetableItem>

    suspend fun fetchTimeTableInfo(startDate: String, endDate: String): List<TimeTableInfo>

    suspend fun insertTimeTable(classes: List<Predavanja>)

}
