package com.tstudioz.fax.fme.feature.timetable.repository.interfaces

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import java.time.LocalDate

interface TimeTableRepositoryInterface {

    suspend fun fetchTimetable(user: String, startDate: LocalDate, endDate: LocalDate): List<Event>

    suspend fun fetchTimeTableInfo(startDate: String, endDate: String): List<TimeTableInfo>

    suspend fun insert(classes: List<Event>)

    suspend fun getCachedEvents(): List<Event>


    }
