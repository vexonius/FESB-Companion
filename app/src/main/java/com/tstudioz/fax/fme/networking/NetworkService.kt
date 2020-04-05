package com.tstudioz.fax.fme.networking

import com.tstudioz.fax.fme.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.koin.java.KoinJavaComponent.inject


class NetworkService : TService {

    private val client: OkHttpClient by inject(OkHttpClient::class.java)

    override suspend fun fetchTimeTable(userName: String, startDate: String, endDate: String): Flow<Result.TimeTableResult> = flow {
        val requestUrl: String =
                "https://raspored.fesb.unist.hr/part/raspored/kalendar?DataType=User&DataId=" + userName + "&MinDate=" + startDate + "&MaxDate=" + endDate

        val request = Request.Builder()
                .url(requestUrl)
                .build()

        val response: Response = client.newCall(request).execute()

        if(response.isSuccessful && response.body != null && response.code < 400)
            emit(Result.TimeTableResult.Success(response.body!!.string()))

        else emit(Result.TimeTableResult.Failure(Throwable("Failed to fetch timetable")))
    }

}