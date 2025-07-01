package com.tstudioz.fax.fme.feature.timetable.view

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
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
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
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
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val snackbarHostState = SnackbarHostState()
    val internetAvailable: LiveData<Boolean> = InternetConnectionObserver.get()

    private val _currentEventShown = MutableLiveData<Event?>(null)
    val currentEventShown: LiveData<Event?> = _currentEventShown

    private var _events = MutableLiveData(timeTableRepository.events.asLiveData().value ?: emptyList())
    var events: LiveData<List<Event>> = _events

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
    }

    fun resetToCurrentWeek() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            timeTableRepository.events.collect { _events.postValue(it) }
        }
        _mondayOfSelectedWeek.postValue(
            LocalDate.now().let { it.minusDays((it.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong()) })
        eventsGlowing.postValue(sharedPreferences[SPKey.EVENTS_GLOW, false])
    }

    fun fetchUserTimetable() {
        if (internetAvailable.value == true) {
            val today = LocalDate.now()
            val startDate: LocalDate = today.minusDays((today.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
            val endDate: LocalDate = today.minusDays((today.dayOfWeek.value - DayOfWeek.SATURDAY.value).toLong())
            fetchUserTimetable(startDate, endDate, startDate, shouldCache = true)
        }
    }

    fun fetchUserTimetable(date: LocalDate) {
        if (internetAvailable.value == true) {
            val startDate: LocalDate = date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
            val endDate: LocalDate = date.minusDays((date.dayOfWeek.value - DayOfWeek.SATURDAY.value).toLong())
            _mondayOfSelectedWeek.value = startDate
            fetchUserTimetable(startDate, endDate, startDate)
        }
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
            val items = timeTableRepository.fetchTimetable(username, startDateFormated, endDateFormated, shouldCache)
            _mondayOfSelectedWeek.postValue(shownWeekMonday)
            _events.postValue(items)
        }
    }

    private fun fetchTimetableAgenda(
        startDate: String = (LocalDate.now().year - 1).toString() + "-8-1",
        endDate: String = (LocalDate.now().year + 1).toString() + "-8-1"
    ) {
        if (internetAvailable.value == true) {
            viewModelScope.launch(Dispatchers.IO + handler) {
                _daysInPeriods.postValue(timeTableRepository.fetchTimeTableCalendar(startDate, endDate))
            }
        }
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

