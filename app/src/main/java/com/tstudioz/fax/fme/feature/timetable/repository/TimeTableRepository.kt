package com.tstudioz.fax.fme.feature.timetable.repository

import android.util.Log
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.timetable.services.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.feature.timetable.dao.interfaces.TimeTableDaoInterface
import com.tstudioz.fax.fme.feature.timetable.parseTimetable
import com.tstudioz.fax.fme.feature.timetable.parseTimetableInfo
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimeTableRepository(
    private val timetableService: TimetableServiceInterface,
    private val timeTableDao: TimeTableDaoInterface
) : TimeTableRepositoryInterface {

    override suspend fun fetchTimetable(user: String, startDate: LocalDate, endDate: LocalDate): List<Event> {

        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val requestUrl = "https://raspored.fesb.unist.hr/part/raspored/kalendar?" +
                "DataType=User&DataId=$user" +
                "&MinDate=${dateFormatter.format(startDate)}" +
                "&MaxDate=${dateFormatter.format(endDate)}"

        return when (val result = timetableService.fetchTimeTable(requestUrl)) {
            is NetworkServiceResult.TimeTableResult.Success -> parseTimetable(result.data)
            is NetworkServiceResult.TimeTableResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                throw Exception("Timetable fetching error")
            }
        }
    }

    override suspend fun fetchTimeTableInfo(startDate: String, endDate: String): List<TimeTableInfo> {

        val requestUrl = "https://raspored.fesb.unist.hr/raspored/periodi-u-mjesecu-json?FromDate=$startDate&ToDate=$endDate"

        return when (val result = timetableService.fetchTimeTable(requestUrl)) {
            is NetworkServiceResult.TimeTableResult.Success -> parseTimetableInfo(result.data)
            is NetworkServiceResult.TimeTableResult.Failure -> {
                Log.e(TAG, "TimetableInfo fetching error")
                throw Exception("TimetableInfo fetching error")
            }
        }
    }

    override suspend fun insert(classes: List<Event>) {
        timeTableDao.insert(classes)
    }

    override suspend fun getCachedEvents(): List<Event> {
        return timeTableDao.getCachedEvents()
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
