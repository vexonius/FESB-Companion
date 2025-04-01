package com.tstudioz.fax.fme.feature.timetable.repository

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.timetable.dao.TimeTableDao
import com.tstudioz.fax.fme.feature.timetable.parseTimetable
import com.tstudioz.fax.fme.feature.timetable.parseTimetableInfo
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.feature.timetable.services.interfaces.TimetableServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TimeTableRepository(
    private val timetableService: TimetableServiceInterface,
    private val timeTableDao: TimeTableDao
) : TimeTableRepositoryInterface {

    private val _events: MutableSharedFlow<List<Event>> = MutableSharedFlow(1)
    override val events: SharedFlow<List<Event>> = _events.asSharedFlow()


    init {
        observeEventsFromCache()
    }

    override suspend fun fetchTimetable(
        user: String,
        startDate: String,
        endDate: String,
        shouldCache: Boolean
    ): List<Event> {
        val params: HashMap<String, String> = hashMapOf(
            "DataType" to "User",
            "DataId" to user,
            "MinDate" to startDate,
            "MaxDate" to endDate
        )

        when (val result = timetableService.fetchTimeTable(params = params)) {
            is NetworkServiceResult.TimeTableResult.Success -> {
                val events = parseTimetable(result.data)

                if (shouldCache) {
                    insert(events)
                }

                return events
            }
            is NetworkServiceResult.TimeTableResult.Failure -> {
                throw Exception("Timetable fetching error")
            }
        }
    }

    override suspend fun fetchTimeTableCalendar(startDate: String, endDate: String): Map<LocalDate, TimeTableInfo> {
        val params: HashMap<String, String> = hashMapOf(
            "FromDate" to startDate,
            "ToDate" to endDate
        )

        return when (val result = timetableService.fetchTimetableCalendar(params = params)) {
            is NetworkServiceResult.TimeTableResult.Success -> parseTimetableInfo(result.data)
            is NetworkServiceResult.TimeTableResult.Failure -> {
                throw Exception("TimetableInfo fetching error")
            }
        }
    }

    override suspend fun getCachedEvents(): List<Event> {
        return timeTableDao.getEvents().map { it.fromRoomObject() }
    }

    private fun observeEventsFromCache() {
        CoroutineScope(Dispatchers.IO).launch {
            timeTableDao.getEventsAsync().collect { events->
                _events.emit(events.map { it.fromRoomObject() })
            }
        }
    }

    private suspend fun insert(classes: List<Event>) {
        timeTableDao.insert(classes.map{it.toRoomObject()})
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}

private fun Long.hasPassedMoreThan(seconds: Long): Boolean {
    return this + seconds * 1000 < System.currentTimeMillis()
}
