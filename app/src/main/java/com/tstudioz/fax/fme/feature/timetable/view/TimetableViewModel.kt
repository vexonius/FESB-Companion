package com.tstudioz.fax.fme.feature.timetable.view

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.timetable.MonthData
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.networking.NetworkUtils
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.SPKey
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
    private val userRepository: UserRepositoryInterface,
    private val networkUtils: NetworkUtils,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val snackbarHostState = SnackbarHostState()

    private val _currentEventShown = MutableLiveData<Event?>(null)
    val currentEventShown: LiveData<Event?> = _currentEventShown

    private var _events = timeTableRepository.events.asLiveData()
    var events: LiveData<List<Event>> = _events

    val displayEvents = MediatorLiveData<List<Event>>()

    val eventsGlowing: MutableLiveData<Boolean> = MutableLiveData(
        sharedPreferences[SPKey.EVENTS_GLOW, false]
    )

    private val _daysInPeriods = MutableLiveData<Map<LocalDate, TimeTableInfo>>(mutableMapOf())
    val daysInPeriods: LiveData<Map<LocalDate, TimeTableInfo>> = _daysInPeriods

    private val _mondayOfSelectedWeek: MutableLiveData<LocalDate> = MutableLiveData<LocalDate>(
        LocalDate.now().let { it.minusDays((it.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong()) })
    val mondayOfSelectedWeek: LiveData<LocalDate> = _mondayOfSelectedWeek

    private val _showWeekChooseMenu = MutableLiveData(false)
    val shownWeekChooseMenu: LiveData<Boolean> = _showWeekChooseMenu

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
        viewModelScope.launch(Dispatchers.Main) { snackbarHostState.showSnackbar("Došlo je do pogreške") }
    }

    init {
        fetchTimetableAgenda()
        fetchCurrentYearUserTimetable()
        displayEvents.addSource(events) { updateDisplayEvents() }
        displayEvents.addSource(_mondayOfSelectedWeek) { updateDisplayEvents() }
    }

    private fun updateDisplayEvents() {
        val eventsValue = events.value ?: return
        val monday = _mondayOfSelectedWeek.value ?: return

        displayEvents.value = eventsValue.filter { event ->
            val eventStart = event.start.toLocalDate()
            eventStart.isAfter(monday.minusDays(1)) && eventStart.isBefore(monday.plusDays(7))
        }
    }

    fun resetToCurrentWeek() {
        _mondayOfSelectedWeek.postValue(
            LocalDate.now().let { it.minusDays((it.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong()) })
        eventsGlowing.postValue(sharedPreferences[SPKey.EVENTS_GLOW, false])
    }

    fun fetchCurrentYearUserTimetable() {
        if (networkUtils.isNetworkAvailable()) {
            val today = LocalDate.now()
            if (today.isBefore(LocalDate.of(today.year, 9, 22))) {
                val startDate: LocalDate = LocalDate.of(today.year - 1, 9, 22)
                val endDate: LocalDate = LocalDate.of(today.year, 10, 1)
                fetchUserTimetable(startDate, endDate)
            } else {
                val startDate: LocalDate = LocalDate.of(today.year, 9, 20)
                val endDate: LocalDate = LocalDate.of(today.year + 1, 10, 1)
                fetchUserTimetable(startDate, endDate)
            }
        }
    }

    private fun fetchUserTimetable(
        startDate: LocalDate,
        endDate: LocalDate,
        shouldCache: Boolean = true
    ) {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val startDateFormated = dateFormatter.format(startDate)
        val endDateFormated = dateFormatter.format(endDate)

        viewModelScope.launch(Dispatchers.IO + handler) {
            val username = userRepository.getCurrentUserName()
            timeTableRepository.fetchTimetable(username, startDateFormated, endDateFormated, shouldCache)
        }
    }

    private fun fetchTimetableAgenda(
        startDate: String = (LocalDate.now().year - 1).toString() + "-8-1",
        endDate: String = (LocalDate.now().year + 1).toString() + "-8-1"
    ) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            _daysInPeriods.postValue(timeTableRepository.fetchTimeTableCalendar(startDate, endDate))
        }
    }

    fun setMondayOfSelectedWeek(date: LocalDate) {
        _mondayOfSelectedWeek.value = date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
    }

    fun showWeekChooseMenu(value: Boolean = true) {
        _showWeekChooseMenu.value = value
    }

    fun showEvent(event: Event) {
        _currentEventShown.value = event
    }

    fun hideEvent() {
        _currentEventShown.value = null
    }

}

