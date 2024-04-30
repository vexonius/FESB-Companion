package com.tstudioz.fax.fme.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.models.data.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.models.data.TimetableItem
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel(
    private val userRepository: UserRepositoryInterface,
    private val timeTableRepository: TimeTableRepositoryInterface
) : ViewModel() {

    val tableGot: MutableLiveData<String> = MutableLiveData()
    var tableGotPerm = MutableLiveData<Boolean>()

    init {
        fetchTimetableInfo()
    }

    private val _showDay = MutableLiveData<Boolean>().apply { value = false }
    private val _showDayEvent = MutableLiveData<Event>()
    private val _lessons = MutableLiveData<List<Event>>().apply { value = emptyList() }
    private val _periods = MutableLiveData<List<TimeTableInfo>>().apply { value = emptyList() }
    private val _shownWeek = MutableLiveData<LocalDate>().apply { value = LocalDate.now()}
    private val _showWeekChooseMenu = MutableLiveData<Boolean>().apply { value = false }
    val showDay: LiveData<Boolean> = _showDay
    val showDayEvent: LiveData<Event> = _showDayEvent
    val lessons: LiveData<List<Event>> = _lessons
    val periods: LiveData<List<TimeTableInfo>> = _periods
    val shownWeek: LiveData<LocalDate> = _shownWeek
    val showWeekChooseMenu: LiveData<Boolean> = _showWeekChooseMenu

    fun fetchUserTimetable(user: User, startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("started Fetching Timetable for user")
                val list = timeTableRepository.fetchTimetable(user.username, startDate, endDate)
                val svaFreshPredavanja = timetableToPredavanje(list)
                val events = timetableToEvent(list)
                _shownWeek.postValue(LocalDate.of(
                    startDate.split("-")[2].toInt(),
                    startDate.split("-")[0].toInt(),
                    startDate.split("-")[1].toInt())
                )
                _lessons.postValue(events)
                insertOrUpdateTimeTable(svaFreshPredavanja)
                tableGotPerm.postValue(true)
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
                tableGotPerm.postValue(false)
            }
        }
    }

    fun fetchTimetableInfo(startDate: String ="2023-1-1", endDate: String = "2025-1-1" ){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("started Fetching TimetableInfo")
                _periods.postValue(timeTableRepository.fetchTimeTableInfo(startDate, endDate))
            } catch (e: Exception) {
                Log.e("Error timetableinfo", e.toString())
            }
        }
    }

    private fun timetableToPredavanje(timetable: List<TimetableItem>): List<Predavanja> {
        val predavanja = mutableListOf<Predavanja>()
        for (l in timetable) {
            val predavanje = Predavanja()
            predavanje.objectId = l.id
            predavanje.id = l.id.toString()
            predavanje.predavanjeIme = l.eventType.type
            predavanje.predmetPredavanja = l.name
            predavanje.rasponVremena = l.timeSpan
            predavanje.grupa = l.group
            predavanje.grupaShort = l.group
            predavanje.dvorana = l.room
            predavanje.detaljnoVrijeme = l.detailDateWithDayName
            predavanje.profesor = l.professor
            val str = "${predavanje.id}${predavanje.predavanjeIme}${predavanje.predmetPredavanja}" +
                    "${predavanje.rasponVremena}${predavanje.grupa}${predavanje.dvorana}${predavanje.detaljnoVrijeme}${predavanje.profesor}"
            val id = UUID.nameUUIDFromBytes(str.toByteArray())
            predavanje.id = id.toString()

            predavanja.add(predavanje)
        }
        return predavanja
    }

    private fun timetableToEvent(timetable: List<TimetableItem>): List<Event> {
        val events = mutableListOf<Event>()
        /*events.add(
            Event(
                id = "0",
                name = "Ponedjeljak",
                fullName = "Ponedjeljak",
                shortName = "P",
                colorId = R.color.blue_nice,
                color = Color.White,
                teacher = "matko",
                type = "Pred",
                groups = "nemaa",
                classroom = "C502",
                classroomShort = "C502",
                start = LocalDateTime.of(
                    LocalDate.of(2024, 5 ,2),
                    LocalTime.of(10, 15)
                ),
                end = LocalDateTime.of(
                    LocalDate.of(2024, 5 ,2),
                    LocalTime.of(11, 0)
                ),
                week = "a",
                description = "C502"
            )
        )*/
        for (l in timetable) {
            val event = Event(
                id = l.id.toString(),
                name = l.name,
                fullName = l.name,
                shortName = l.name.split(" ").toTypedArray().let {
                    val title = StringBuilder()
                    for (part in it)
                        title.append(part[0].uppercase())
                    title.toString()
                } ?: "",
                colorId = getBoja(l.eventType.type),
                color = Color.White,
                teacher = l.professor,
                type = l.eventType.type,
                groups = l.group,
                classroom = l.room,
                classroomShort = l.room,
                start = LocalDateTime.of(
                    LocalDate.of(
                        l.startDate.split("-")[0].toInt(),
                        l.startDate.split("-")[1].toInt(),
                        l.startDate.split("-")[2].toInt()
                    ),
                    LocalTime.of(l.startHour, l.startMin)
                ),
                end = LocalDateTime.of(
                    LocalDate.of(
                        l.endDate.split("-")[0].toInt(),
                        l.endDate.split("-")[1].toInt(),
                        l.endDate.split("-")[2].toInt()
                    ),
                    LocalTime.of(l.endHour, l.endMin)
                ),
                week = "",
                description = l.room
            )
            events.add(event)
        }
        return events
    }

    private fun getBoja(predavanjeIme: String): Int {
        return when (predavanjeIme) {
            "Predavanje" -> R.color.blue_nice
            "Auditorne vježbe" -> R.color.green_nice
            "Kolokvij" -> R.color.purple_nice
            "Laboratorijske vježbe" -> R.color.red_nice
            "Konstrukcijske vježbe" -> R.color.grey_nice
            "Seminar" -> R.color.blue_nice
            "Ispit" -> R.color.purple_dark
            else -> {
                R.color.blue_nice
            }
        }
    }

    fun fetchUserTimetableTemp(user: User, startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("started Fetching Timetable for user")
                val list = timeTableRepository.fetchTimetable(user.username, startDate, endDate)
                tableGot.postValue("fetched")
                delay(100)
                tableGot.postValue("fetchedold")
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
                tableGot.postValue("error")
            }
        }
    }

    fun showWeekChooseMenu(value:Boolean = true) {
        _showWeekChooseMenu.postValue(value)
    }

    fun showDay(event: Event) {
        _showDayEvent.postValue(event)
        _showDay.postValue(true)
    }

    fun hideDay() {
        _showDay.postValue(false)
    }

    private suspend fun insertOrUpdateTimeTable(classes: List<Predavanja>) {
        timeTableRepository.insertTimeTable(classes)
    }

}

