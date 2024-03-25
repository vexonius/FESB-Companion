package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.Result

interface TimetableServiceInterface {

    suspend fun fetchTimeTable(
        userName: String,
        startDate: String,
        endDate: String
    ): Result.TimeTableResult

}