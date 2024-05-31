package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface TimetableServiceInterface {

    suspend fun fetchTimeTable(
        url: String
    ): NetworkServiceResult.TimeTableResult
}
