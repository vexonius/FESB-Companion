package com.tstudioz.fax.fme.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.IksicaSaldo
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.models.data.IksicaRepositoryInterface
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.models.util.PreferenceHelper.get
import com.tstudioz.fax.fme.models.util.PreferenceHelper.set
import com.tstudioz.fax.fme.models.util.SPKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel(
    private val userRepository: UserRepositoryInterface,
    private val timeTableRepository: TimeTableRepositoryInterface,
    private val dbManager: DatabaseManagerInterface,
    private val sharedPreferences: SharedPreferences,
    private val iksicaRepository: IksicaRepositoryInterface
) : ViewModel() {

    private val _showEvent = MutableLiveData<Boolean>(false)
    private val _showEventContent = MutableLiveData<Event>()
    private val _lessonsToShow = MutableLiveData<List<Event>>().apply { value = emptyList() }
    private val _lessonsPerm = MutableLiveData<List<Event>>().apply { value = emptyList() }
    private val _periods = MutableLiveData<List<TimeTableInfo>>().apply { value = emptyList() }
    private val _shownWeek = MutableLiveData<LocalDate>().apply {
        val now = LocalDate.now().plusDays(1)
        val start = now.dayOfWeek.value
        value = (sharedPreferences[SPKey.SHOWN_WEEK, ""].let {
            if (it != "") {
                LocalDate.parse(it)
            } else null
        } ?: now.plusDays((1 - start).toLong()))
    }
    private val _showWeekChooseMenu = MutableLiveData<Boolean>().apply { value = false }
    val showDay: LiveData<Boolean> = _showEvent
    val showDayEvent: LiveData<Event> = _showEventContent
    val lessonsToShow: LiveData<List<Event>> = _lessonsToShow
    val lessonsPerm: LiveData<List<Event>> = _lessonsPerm
    val periods: LiveData<List<TimeTableInfo>> = _periods
    val shownWeek: LiveData<LocalDate> = _shownWeek
    val shownWeekChooseMenu: LiveData<Boolean> = _showWeekChooseMenu

    private var _loadingTxt = MutableLiveData<String>()
    private val _iksicaSaldo = MutableLiveData<IksicaSaldo>()
    private val _studentDataIksica = MutableLiveData<StudentDataIksica>()
    val loadingTxt: LiveData<String> = _loadingTxt
    val iksicaSaldo: LiveData<IksicaSaldo> = _iksicaSaldo
    val studentDataIksica: LiveData<StudentDataIksica> = _studentDataIksica

    init {
        fetchTimetableInfo()
        viewModelScope.launch(Dispatchers.IO) {
            _lessonsPerm.postValue(timeTableRepository.getCachedEvents())
        }
    }

    fun fetchUserTimetable(
        user: User = User(sharedPreferences.getString("username", "") ?: "", ""),
        startDate: LocalDate,
        endDate: LocalDate,
        shownWeekMonday: LocalDate
    ) {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val startDate = dateFormatter.format(startDate)
        val endDate = dateFormatter.format(endDate)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val events = timeTableRepository.fetchTimetable(user.username, startDate, endDate)
                _shownWeek.postValue(shownWeekMonday)
                sharedPreferences[SPKey.SHOWN_WEEK] = shownWeekMonday.toString()
                _lessonsToShow.postValue(events)
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
            }
        }
    }

    fun fetchUserTimetableCurrentWeekAndSave(
        user: User,
        startDate: LocalDate,
        endDate: LocalDate,
        shownWeekMonday: LocalDate
    ) {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val startDate = dateFormatter.format(startDate)
        val endDate = dateFormatter.format(endDate)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("started Fetching Timetable for user")
                val events = timeTableRepository.fetchTimetable(user.username, startDate, endDate)
                _shownWeek.postValue(shownWeekMonday)
                sharedPreferences[SPKey.SHOWN_WEEK] = shownWeekMonday.toString()
                sharedPreferences[SPKey.LAST_FETCHED] =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                _lessonsPerm.postValue(events)
                timeTableRepository.insert(events)
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
            }
        }
    }

    private fun fetchTimetableInfo(
        startDate: String = (LocalDate.now().year - 1).toString() + "-8-1",
        endDate: String = (LocalDate.now().year + 1).toString() + "-8-1"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = timeTableRepository.fetchTimeTableCalendar(startDate, endDate)
                _periods.postValue(result)
            } catch (e: Exception) {
                Log.e("Error timetableinfo", e.toString())
            }
        }
    }

    fun loginIksica() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadingTxt.postValue("Getting AuthState...")
                iksicaRepository.getAuthState()
                _loadingTxt.postValue("Logging in...")
                iksicaRepository.login(
                    (sharedPreferences.getString("username", "") + "@fesb.hr") ?: "",
                    sharedPreferences.getString("password", "") ?: ""
                )
                _loadingTxt.postValue("Getting ASP.NET Session...")
                val (iksicaSaldo, studentDataIksica) = iksicaRepository.getAspNetSessionSAML()
                _iksicaSaldo.postValue(iksicaSaldo)
                _studentDataIksica.postValue(studentDataIksica)
                _loadingTxt.postValue("Parsing Data...")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun showThisWeeksEvents() {
        _lessonsToShow.postValue(_lessonsPerm.value)
    }

    fun showWeekChooseMenu(value: Boolean = true) {
        _showWeekChooseMenu.postValue(value)
    }

    fun showEvent(event: Event) {
        _showEventContent.postValue(event)
        _showEvent.postValue(true)
    }

    fun hideEvent() {
        _showEvent.postValue(false)
    }

}

