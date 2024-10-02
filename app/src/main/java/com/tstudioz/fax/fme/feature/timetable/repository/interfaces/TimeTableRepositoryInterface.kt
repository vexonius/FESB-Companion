package com.tstudioz.fax.fme.feature.timetable.repository.interfaces

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo

interface TimeTableRepositoryInterface {

    var lastFetched: Long

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String, shouldCache: Boolean): List<Event>

    suspend fun fetchTimeTableCalendar(startDate: String, endDate: String): List<TimeTableInfo>

    suspend fun getCachedEvents(): List<Event>

}
