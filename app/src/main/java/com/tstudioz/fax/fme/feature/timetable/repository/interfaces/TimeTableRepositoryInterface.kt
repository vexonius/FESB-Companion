package com.tstudioz.fax.fme.feature.timetable.repository.interfaces

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDate

interface TimeTableRepositoryInterface {

    val events: SharedFlow<List<Event>>

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String, shouldCache: Boolean): List<Event>

    suspend fun fetchTimeTableCalendar(startDate: String, endDate: String): MutableMap<LocalDate, TimeTableInfo>

    suspend fun getCachedEvents(): List<Event>

}
