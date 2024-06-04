package com.tstudioz.fax.fme.feature.timetable.services.interfaces

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface TimetableServiceInterface {

    suspend fun fetchTimeTable(
        url: String
    ): NetworkServiceResult.TimeTableResult

}
