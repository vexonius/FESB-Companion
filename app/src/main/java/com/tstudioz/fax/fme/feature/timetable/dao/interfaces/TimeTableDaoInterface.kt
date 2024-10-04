package com.tstudioz.fax.fme.feature.timetable.dao.interfaces

import com.tstudioz.fax.fme.database.models.Event
import kotlinx.coroutines.flow.Flow

interface TimeTableDaoInterface {

    suspend fun insert(classes: List<Event> )

    suspend fun getEvents(): List<Event>

    suspend fun getEventsAsync(): Flow<List<Event>>

    }