package com.tstudioz.fax.fme.feature.timetable.repository.interfaces

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import kotlinx.coroutines.flow.SharedFlow

interface TimeTableRepositoryInterface {

    val lastFetched: Long

    val events: SharedFlow<List<Event>>

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String, shouldCache: Boolean): List<Event>

    suspend fun fetchTimeTableCalendar(startDate: String, endDate: String): List<TimeTableInfo>

    suspend fun getCachedEvents(): List<Event>

}
