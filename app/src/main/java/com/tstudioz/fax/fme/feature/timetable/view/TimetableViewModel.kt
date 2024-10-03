package com.tstudioz.fax.fme.feature.timetable.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class TimetableViewModel(
    private val timeTableRepository: TimeTableRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val _currentEventShown = MutableLiveData<Event?>(null)
    private val _events = MutableLiveData<List<Event>>(emptyList())
    private val _periods = MutableLiveData<List<TimeTableInfo>>(emptyList())
    private val _mondayOfSelectedWeek: MutableLiveData<LocalDate> = MutableLiveData<LocalDate>().apply {
        val date = LocalDate.now()
        value = date.minusDays(((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong()))
    }
    private val _showWeekChooseMenu = MutableLiveData(false)

    val showDayEvent: LiveData<Event?> = _currentEventShown
    val events: LiveData<List<Event>> = _events
    val periods: LiveData<List<TimeTableInfo>> = _periods
    val shownWeekChooseMenu: LiveData<Boolean> = _showWeekChooseMenu
    val mondayOfSelectedWeek: LiveData<LocalDate> = _mondayOfSelectedWeek

    val monthData = MutableLiveData(
        MonthData(
            currentMonth = YearMonth.now(),
            startMonth = YearMonth.now().minusMonths(100),
            endMonth = YearMonth.now().plusMonths(100),
            firstDayOfWeek = firstDayOfWeekFromLocale()
        )
    )

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Error timetable", exception.toString())
    }

    init {
        getCachedEvents()
        fetchUserTimetable()
        fetchTimetableAgenda()
    }

    fun fetchUserTimetable() {
        val today = LocalDate.now()
        val startDate: LocalDate = today.minusDays((today.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
        val endDate: LocalDate = today.minusDays((today.dayOfWeek.value - DayOfWeek.SATURDAY.value).toLong())
        fetchUserTimetable(startDate, endDate, startDate, shouldCache = true)
    }

    fun fetchUserTimetable(date: LocalDate) {
        val startDate: LocalDate = date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
        val endDate: LocalDate = date.minusDays((date.dayOfWeek.value - DayOfWeek.SATURDAY.value).toLong())
        fetchUserTimetable(startDate, endDate, startDate)
    }

    private fun fetchUserTimetable(
        startDate: LocalDate,
        endDate: LocalDate,
        shownWeekMonday: LocalDate,
        shouldCache: Boolean = false
    ) {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val startDateFormated = dateFormatter.format(startDate)
        val endDateFormated = dateFormatter.format(endDate)

        viewModelScope.launch(Dispatchers.IO + handler) {
            val username = userRepository.getCurrentUserName()
            val events = timeTableRepository.fetchTimetable(username, startDateFormated, endDateFormated, shouldCache)
            _mondayOfSelectedWeek.postValue(shownWeekMonday)
            _events.postValue(events)
        }
    }

    private fun getCachedEvents() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val cachedItems = timeTableRepository.getCachedEvents()
            _events.postValue(cachedItems)
        }
    }

    private fun fetchTimetableAgenda(
        startDate: String = (LocalDate.now().year - 1).toString() + "-8-1",
        endDate: String = (LocalDate.now().year + 1).toString() + "-8-1"
    ) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val result = timeTableRepository.fetchTimeTableCalendar(startDate, endDate)
            _periods.postValue(result)
        }
    }

    fun showThisWeeksEvents() {
        fetchUserTimetable()
    }

    fun showWeekChooseMenu(value: Boolean = true) {
        _showWeekChooseMenu.postValue(value)
    }

    fun showEvent(event: Event) {
        _currentEventShown.postValue(event)
    }

    fun hideEvent() {
        _currentEventShown.postValue(null)
    }

}

