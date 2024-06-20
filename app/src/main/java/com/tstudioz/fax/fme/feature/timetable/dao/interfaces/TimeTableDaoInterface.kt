package com.tstudioz.fax.fme.feature.timetable.dao.interfaces

import com.tstudioz.fax.fme.database.models.Event

interface TimeTableDaoInterface {

    suspend fun insert(classes: List<Event> )

    suspend fun getCachedEvents(): List<Event>

}