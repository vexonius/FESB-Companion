package com.tstudioz.fax.fme.models.data

import android.util.Log
import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.networking.NetworkService
import com.tstudioz.fax.fme.networking.PortalService
import com.tstudioz.fax.fme.models.util.parseTimetable
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject


@InternalCoroutinesApi
class Repository {

    private val service: PortalService by inject(PortalService::class.java)
    private val networkService: NetworkService by inject(NetworkService::class.java)

    suspend fun attemptLogin(user: User): User {
        when (val result = service.loginUser(user)) {
            is Result.LoginResult.Success -> return(result.data)
            is Result.LoginResult.Failure -> {
                Log.d(TAG, "Doslo je do pogreske")
                return(User("","",""))
            }
        }

    }

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): Flow<List<TimetableItem>> = flow {
        networkService.fetchTimeTable(user, startDate, endDate).collect { result ->
            when(result){
                is Result.TimeTableResult.Success -> emit(parseTimetable(result.data))
                is Result.TimeTableResult.Failure -> Log.e(TAG, "Timetable fetching error")
            }
        }
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }


}