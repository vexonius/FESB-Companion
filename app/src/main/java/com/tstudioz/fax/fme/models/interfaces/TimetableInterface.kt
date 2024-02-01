package com.tstudioz.fax.fme.models.interfaces

import com.tstudioz.fax.fme.models.Result


/**
 * Created by etino7 on 3.4.2020..
 */
interface TimetableInterface {

    suspend fun fetchTimeTable(userName: String, startDate: String, endDate: String): Result.TimeTableResult
}