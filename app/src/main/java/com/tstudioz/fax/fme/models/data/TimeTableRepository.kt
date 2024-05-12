package com.tstudioz.fax.fme.models.data

import android.util.Log
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.models.util.parseTimetable

class TimeTableRepository(
    private val timetableService: TimetableServiceInterface,
    private val timeTableDao: TimeTableDaoInterface
    ): TimeTableRepositoryInterface {

    override suspend fun fetchTimetable(user: String, startDate: String, endDate: String): List<TimetableItem> {
        return when(val result = timetableService.fetchTimeTable(user, startDate, endDate)){
            is NetworkServiceResult.TimeTableResult.Success -> parseTimetable(result.data)
            is NetworkServiceResult.TimeTableResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                throw Exception("Timetable fetching error")
            }
        }
    }

    override suspend fun insertTimeTable(classes: List<Predavanja>) {
        timeTableDao.insert(classes)
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
