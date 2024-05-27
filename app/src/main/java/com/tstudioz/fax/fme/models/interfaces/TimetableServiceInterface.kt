package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.NetworkServiceResult
import java.time.LocalDate

interface TimetableServiceInterface {

    suspend fun fetchTimeTable(
        userName: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): NetworkServiceResult.TimeTableResult

    suspend fun fetchTimeTableInfo(
        startDate: String,
        endDate: String
    ): NetworkServiceResult.TimeTableResult

}
