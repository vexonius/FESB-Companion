package com.tstudioz.fax.fme.models.data

import android.util.Log
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.services.PrisutnostService
import com.tstudioz.fax.fme.models.services.TimetableNetworkService
import com.tstudioz.fax.fme.models.services.UserService
import com.tstudioz.fax.fme.models.util.parseTimetable
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.java.KoinJavaComponent.inject


@InternalCoroutinesApi
class Repository {

    private val service: UserService by inject(UserService::class.java)
    private val timetableNetworkService: TimetableNetworkService by inject(TimetableNetworkService::class.java)
    private val prisutnostService = PrisutnostService()
    private val timeTableDao: TimeTableDao = TimeTableDao()

    suspend fun attemptLogin(user: User): User {
        return when (val result = service.loginUser(user)) {
            is Result.LoginResult.Success -> (result.data)
            is Result.LoginResult.Failure -> {
                Log.d(TAG, "Doslo je do pogreske")
                (User("","",""))
            }
        }
    }

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<TimetableItem> {
        return when(val result = timetableNetworkService.fetchTimeTable(user, startDate, endDate)){
            is Result.TimeTableResult.Success -> parseTimetable(result.data)
            is Result.TimeTableResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                emptyList()
            }
        }
    }

    suspend fun fetchPrisutnost(): Result.PrisutnostResult {
        return when (prisutnostService.fetchPrisutnost()){
            is Result.PrisutnostResult.Success -> Result.PrisutnostResult.Success(true)
            is Result.PrisutnostResult.Failure -> Result.PrisutnostResult.Failure(Throwable())
        }
    }

    fun insertOrUpdateTimeTable(freshPredavanja: MutableList<Predavanja>) {
        timeTableDao.insertOrUpdateTimeTable(freshPredavanja)
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }


}