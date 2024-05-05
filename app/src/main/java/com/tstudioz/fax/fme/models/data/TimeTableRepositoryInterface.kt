package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.database.models.TimetableItem

interface TimeTableRepositoryInterface {

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<Event>

    suspend fun fetchTimeTableInfo(startDate: String, endDate: String): List<TimeTableInfo>

    suspend fun insertTimeTable(classes: List<Event>)

}
