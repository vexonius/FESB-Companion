package com.tstudioz.fax.fme.data

import com.orhanobut.hawk.Hawk
import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.TimetableItem
import com.tstudioz.fax.fme.models.User
import com.tstudioz.fax.fme.networking.NetworkService
import com.tstudioz.fax.fme.networking.PortalService
import com.tstudioz.fax.fme.util.parseTimetable
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber


@InternalCoroutinesApi
class Repository {

    private val service: PortalService by inject(PortalService::class.java)
    private val networkService: NetworkService by inject(NetworkService::class.java)

    suspend fun attemptLogin(): Flow<User> = flow {
        service.loginUser().collect { result ->
            when (result) {
                is Result.LoginResult.Success -> emit(result.data)
                is Result.LoginResult.Failure -> Timber.e(result.throwable, "Doslo je do pogreske")
            }
        }

    }

    suspend fun fetchTimetable(user: String, startDate: String, endDate: String): Flow<List<TimetableItem>> = flow {
        networkService.fetchTimeTable(user, startDate, endDate).collect { result ->
            when (result) {
                is Result.TimeTableResult.Success -> emit(parseTimetable(result.data))
                is Result.TimeTableResult.Failure -> Timber.e(result.throwable, "Timetable fetching error")
            }
        }
    }

    suspend fun compareTimetables(list: List<TimetableItem>): Flow<List<TimetableItem>> = flow {
        var oldList: List<TimetableItem>? = null
        oldList = Hawk.get<List<TimetableItem>>("raspored_cache")
        Timber.d("old list size ${oldList?.size}")

        if (oldList == null || oldList.isEmpty()) {
            emit(list)
        } else {
            val oldListIDs = oldList.map { it.id }
            val difference: List<TimetableItem> = list.filter { it.id !in oldListIDs }
            Hawk.put("raspored_cache", list)

            emit(difference)
        }
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }


}