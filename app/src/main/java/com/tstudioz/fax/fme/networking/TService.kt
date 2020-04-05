package com.tstudioz.fax.fme.networking

import com.tstudioz.fax.fme.models.Result
import kotlinx.coroutines.flow.Flow


/**
 * Created by etino7 on 3.4.2020..
 */
interface TService {

    suspend fun fetchTimeTable(userName: String, startDate: String, endDate: String): Flow<Result.TimeTableResult>
}