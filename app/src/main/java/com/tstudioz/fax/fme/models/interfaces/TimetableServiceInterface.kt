package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface TimetableServiceInterface {

    suspend fun fetchTimeTable(
        userName: String,
        startDate: String,
        endDate: String
    ): NetworkServiceResult.TimeTableResult

    suspend fun fetchTimeTableInfo(
        startDate: String,
        endDate: String
    ): NetworkServiceResult.TimeTableResult


}
