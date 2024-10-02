package com.tstudioz.fax.fme.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.feature.timetable.view.MonthData
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.PreferenceHelper.set
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
class MainViewModel(
    private val timeTableRepository: TimeTableRepositoryInterface,
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val _currentEventShown = MutableLiveData<Event?>(null)
    private val _lessonsToShow = MutableLiveData<List<Event>>(emptyList())
    private val _lessonsPerm = MutableLiveData<List<Event>>(emptyList())
    private val _periods = MutableLiveData<List<TimeTableInfo>>(emptyList())
    private val _shownWeek = MutableLiveData<LocalDate>().apply {
        val now = LocalDate.now().plusDays(1)
        val start = now.dayOfWeek.value
        value = (sharedPreferences[SPKey.SHOWN_WEEK, ""].let {
            if (it != "") {
                LocalDate.parse(it)
            } else null
        } ?: now.plusDays((1 - start).toLong()))
    }
    private val _showWeekChooseMenu = MutableLiveData(false)
    private val _lastFetched = MutableLiveData<String>().apply {
        value = sharedPreferences[SPKey.LAST_FETCHED, ""]
    }

    val showDayEvent: LiveData<Event?> = _currentEventShown
    val lessonsToShow: LiveData<List<Event>> = _lessonsToShow
    val lessonsPerm: LiveData<List<Event>> = _lessonsPerm
    val periods: LiveData<List<TimeTableInfo>> = _periods
    val shownWeek: LiveData<LocalDate> = _shownWeek
    val shownWeekChooseMenu: LiveData<Boolean> = _showWeekChooseMenu
    val lastFetched: LiveData<String> = _lastFetched

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
            _shownWeek.postValue(shownWeekMonday)
            sharedPreferences[SPKey.SHOWN_WEEK] = shownWeekMonday.toString()
            _lessonsToShow.postValue(events)
        }
    }

    private fun getCachedEvents() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val cachedItems = timeTableRepository.getCachedEvents()
            _lessonsPerm.postValue(cachedItems)
            _lessonsToShow.postValue(cachedItems)
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
        _lessonsToShow.postValue(_lessonsPerm.value)
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

