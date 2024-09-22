package com.tstudioz.fax.fme.feature.timetable.services.interfaces

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface TimetableServiceInterface {

    suspend fun fetchTimeTable(
        params: HashMap<String, String>
    ): NetworkServiceResult.TimeTableResult

    suspend fun fetchTimetableCalendar(
        params: HashMap<String, String>
    ): NetworkServiceResult.TimeTableResult

}
