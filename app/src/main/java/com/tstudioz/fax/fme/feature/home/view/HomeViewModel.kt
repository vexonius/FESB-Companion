package com.tstudioz.fax.fme.feature.home.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.database.models.toNote
import com.tstudioz.fax.fme.database.models.toNoteRealm
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.home.WeatherDisplay
import com.tstudioz.fax.fme.feature.home.codeToDisplay
import com.tstudioz.fax.fme.feature.home.repository.NoteRepositoryInterface
import com.tstudioz.fax.fme.feature.home.weatherSymbolKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.Locale


@InternalCoroutinesApi
class HomeViewModel(
    application: Application,
    private val repository: UserRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface
) : AndroidViewModel(application) {

    private var _forecastGot = MutableLiveData<Boolean>()
    private val _weatherDisplay = MutableLiveData<WeatherDisplay>()
    private val _notes = MutableLiveData<List<Note>>()
    val weatherDisplay: LiveData<WeatherDisplay> = _weatherDisplay
    val forecastGot: LiveData<Boolean> = _forecastGot
    val notes: LiveData<List<Note>> = _notes

    fun getForecast(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weather = repository.fetchWeatherDetails(url)
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
                    _forecastGot.postValue(true)
                } else {
                    _forecastGot.postValue(false)
                }
            }
            catch (e: Exception) {
                _forecastGot.postValue(false)
            }
        }
    }

    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val notes = noteRepository.getNotes()
            _notes.postValue(notes.map { it.toNote() })
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
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.insert(note.toNoteRealm())
        }
    }

    fun delete(note: Note) {
        _notes.value = _notes.value?.minus(note)
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.delete(note.toNoteRealm())
        }
    }
}
