package com.tstudioz.fax.fme.feature.home.view

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.feature.home.WeatherDisplay
import com.tstudioz.fax.fme.feature.home.codeToDisplay
import com.tstudioz.fax.fme.feature.home.repository.NoteRepositoryInterface
import com.tstudioz.fax.fme.feature.home.repository.WeatherRepositoryInterface
import com.tstudioz.fax.fme.feature.home.weatherSymbolKeys
import com.tstudioz.fax.fme.feature.timetable.repository.interfaces.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.networking.NetworkUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.core.net.toUri

@InternalCoroutinesApi
class HomeViewModel(
    private val application: Application,
    private val noteRepository: NoteRepositoryInterface,
    private val weatherRepository: WeatherRepositoryInterface,
    private val timeTableRepository: TimeTableRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
) : AndroidViewModel(application) {

    private val networkUtils: NetworkUtils by inject(NetworkUtils::class.java)

    val snackbarHostState: SnackbarHostState = SnackbarHostState()
    private val _weatherDisplay = MutableLiveData<WeatherDisplay>()
    private val _notes = MutableLiveData<List<Note>>()
    val nameOfUser = MutableLiveData<String>()
    val weatherDisplay: LiveData<WeatherDisplay> = _weatherDisplay
    val notes: LiveData<List<Note>> = _notes
    val events: LiveData<List<Event>> = timeTableRepository.events.asLiveData()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("HomeViewModel", "Caught $exception")
        viewModelScope.launch(Dispatchers.Main) { snackbarHostState.showSnackbar("Došlo je do pogreške") }
    }

    init {
        getNotes()
        getForecast()
        viewModelScope.launch(Dispatchers.IO + handler) {
            val name = userRepository.getCurrentUser().fullName.split(" ") .firstOrNull() ?: ""
            nameOfUser.postValue(name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            })
        }
    }

    private fun getForecast() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            if (networkUtils.isNetworkAvailable()) {
                try {
                    val weather = weatherRepository.fetchWeatherDetails()
                    if (weather != null) {
                        val forecastInstantDetails = weather.properties?.timeseries?.first()?.data?.instant?.details
                        val forecastNextOneHours = weather.properties?.timeseries?.first()?.data?.next1Hours
                        val forecastNextOneHoursDetails = forecastNextOneHours?.details
                        val unparsedSummary = forecastNextOneHours?.summary?.symbolCode
                        val weatherSymbol = weatherSymbolKeys[unparsedSummary]
                        val iconName = "_" + weatherSymbol?.first.toString() + weatherSymbol?.second
                        val summary = codeToDisplay[weatherSymbol?.first]?.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                        _weatherDisplay.postValue(
                            WeatherDisplay(
                                "Split",
                                forecastInstantDetails?.airTemperature ?: 20.0,
                                forecastInstantDetails?.relativeHumidity ?: 0.0,
                                forecastInstantDetails?.windSpeed ?: 0.0,
                                forecastNextOneHoursDetails?.precipitationAmount ?: 0.00,
                                iconName,
                                summary ?: ""
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.d("HomeViewModel", "Caught $e")
                    snackbarHostState.showSnackbar("Došlo je do pogreške pri dohvaćanju vremenske prognoze")
                }
            } else {
                snackbarHostState.showSnackbar("Niste povezani")
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
        val appPackageName = "com.ugovori.studentskiugovori"
        val intent = application.packageManager.getLaunchIntentForPackage(appPackageName)
        if (intent != null) {
            application.startActivity(intent)
        } else {
            try {
                application.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "market://details?id=$appPackageName".toUri()
                    )
                )
            } catch (ex: ActivityNotFoundException) {
                application.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                    )
                )
            }
        }
    }
}
