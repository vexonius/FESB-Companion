package com.tstudioz.fax.fme.models.data

import android.util.Log
import com.tstudioz.fax.fme.database.Dolazak
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
    private val prisutnostDao: PrisutnostDao = PrisutnostDao()

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
        return prisutnostService.fetchPrisutnost()
    }

    fun insertOrUpdateTimeTable(freshPredavanja: MutableList<Predavanja>) {
        timeTableDao.insertOrUpdateTimeTable(freshPredavanja)
    }
    fun insertOrUpdatePrisutnost(freshPris: MutableList<Dolazak>) {
        prisutnostDao.insertOrUpdatePrisutnost(freshPris)
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }


}