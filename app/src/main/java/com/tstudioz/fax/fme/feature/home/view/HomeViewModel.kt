package com.tstudioz.fax.fme.feature.home.view

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.feature.home.models.WeatherDisplay
import com.tstudioz.fax.fme.feature.home.repository.NoteRepositoryInterface
import com.tstudioz.fax.fme.feature.home.repository.WeatherRepositoryInterface
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@InternalCoroutinesApi
class HomeViewModel(
    application: Application,
    private val noteRepository: NoteRepositoryInterface,
    private val weatherRepository: WeatherRepositoryInterface,
    private val timeTableRepository: TimeTableRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
) : AndroidViewModel(application) {

    val internetAvailable: LiveData<Boolean> = InternetConnectionObserver.get()

    val snackbarHostState: SnackbarHostState = SnackbarHostState()
    private val _weatherDisplay = MutableLiveData<WeatherDisplay>()
    private val _notes = MutableLiveData<List<Note>>()
    val nameOfUser = MutableLiveData<String>()
    val weatherDisplay: LiveData<WeatherDisplay> = _weatherDisplay
    val notes: LiveData<List<Note>> = _notes
    val events: LiveData<List<Event>> = timeTableRepository.events.asLiveData()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("HomeViewModel", "Caught $exception")
        viewModelScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(
                getApplication<Application>().applicationContext.getString(
                    R.string.general_error
                )
            )
        }
    }

    init {
        getNotes()
        getForecast()
        loadUsersName()
    }

    private fun getForecast() {
        if (internetAvailable.value == false) return
        viewModelScope.launch(Dispatchers.IO + handler) {
            try {
                weatherRepository.fetchWeatherDetails()?.let { _weatherDisplay.postValue(it) }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar(getApplication<Application>().applicationContext.getString(R.string.weather_error))
            }
        }
    }

    fun insert(note: Note) {
        if (_notes.value?.any { it.id == note.id } == true)
            _notes.value?.map {
                if (it.id == note.id) {
                    it.checked = note.checked
                }
            }
        else {
            _notes.value = _notes.value?.plus(note)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            noteRepository.insert(note)
        }
    }

    fun delete(note: Note) {
        _notes.value = _notes.value?.minus(note)
        viewModelScope.launch(Dispatchers.IO + handler) {
            noteRepository.delete(note)
        }
    }

    fun fetchDailyTimetable() {
        if (internetAvailable.value == false) return
        val date = LocalDate.now()
        val startDate: LocalDate = date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
        val endDate: LocalDate = date.minusDays((date.dayOfWeek.value - DayOfWeek.SATURDAY.value).toLong())
        fetchDailyTimetable(startDate, endDate)
    }

    private fun getNotes() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val notes = noteRepository.getNotes()
            _notes.postValue(notes)
        }
    }

    private fun fetchDailyTimetable(
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val startDateFormated = dateFormatter.format(startDate)
        val endDateFormated = dateFormatter.format(endDate)

        viewModelScope.launch(Dispatchers.IO + handler) {
            val username = userRepository.getCurrentUserName()
            timeTableRepository.fetchTimetable(username, startDateFormated, endDateFormated, true)
        }
    }

    fun launchStudentskiUgovoriApp() {
        val context = getApplication<Application>().applicationContext
        val appPackageName = "com.ugovori.studentskiugovori"
        val intent = context.packageManager.getLaunchIntentForPackage(appPackageName) ?: Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
        )
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun loadUsersName() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val name = userRepository.getCurrentUser().fullName.split(" ").firstOrNull() ?: ""
            nameOfUser.postValue(name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            })
        }
    }
}
