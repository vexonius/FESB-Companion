package com.tstudioz.fax.fme.viewmodel

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.models.data.TimeTableRepositoryInterface
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
import kotlin.coroutines.coroutineContext

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel(
    private val userRepository: UserRepositoryInterface,
    private val timeTableRepository: TimeTableRepositoryInterface
) : ViewModel() {

    val tableGot: MutableLiveData<String> = MutableLiveData()
    var tableGotPerm = MutableLiveData<Boolean>()
    private val _permPredavanja: MutableLiveData<List<Predavanja>> = MutableLiveData(listOf())
    val permPredavanja: LiveData<List<Predavanja>> = _permPredavanja
    private val _tempPredavanja: MutableLiveData<List<Predavanja>> = MutableLiveData(listOf())
    val tempPredavanja: LiveData<List<Predavanja>> = _tempPredavanja

    private val _lessons = MutableLiveData<List<Event>>().apply { value = emptyList() }
    val lessons: LiveData<List<Event>> = _lessons

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchUserTimetable(user: User, startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("started Fetching Timetable for user")
                val list = timeTableRepository.fetchTimetable(user.username, startDate, endDate)
                val svaFreshPredavanja = mutableListOf<Predavanja>()
                val events = mutableListOf<Event>()
                list.forEach { println(it.name) }
                for (l in list) {
                    val predavanja = Predavanja()
                    predavanja.objectId = l.id
                    predavanja.id = l.id.toString()
                    predavanja.predavanjeIme = l.eventType.type
                    predavanja.predmetPredavanja = l.name
                    predavanja.rasponVremena = l.timeSpan
                    predavanja.grupa = l.group
                    predavanja.grupaShort = l.group
                    predavanja.dvorana = l.room
                    predavanja.detaljnoVrijeme = l.detailDateWithDayName
                    predavanja.profesor = l.professor
                    val str = "${predavanja.id}${predavanja.predavanjeIme}${predavanja.predmetPredavanja}" +
                            "${predavanja.rasponVremena}${predavanja.grupa}${predavanja.dvorana}${predavanja.detaljnoVrijeme}${predavanja.profesor}"
                    val id = UUID.nameUUIDFromBytes(str.toByteArray())
                    predavanja.id = id.toString()

                    svaFreshPredavanja.add(predavanja)
                }

                "data class Event(\n" +
                        "    val id : String,\n" +
                        "    val name: String,\n" +
                        "    val fullName : String,\n" +
                        "    val shortName: String,\n" +
                        "    val color: Color,\n" +
                        "    val teacher: String = \"\",\n" +
                        "    val groups: String = \"\",\n" +
                        "    val classroom: String = \"\",\n" +
                        "    val classroomShort: String = \"\",\n" +
                        "    val start: LocalDateTime,\n" +
                        "    val end: LocalDateTime,\n" +
                        "    val week: String = \"\",\n" +
                        "    val description: String? = null,\n" +
                        ")"

                for (l in list) {
                    val event = Event(
                        id = l.id.toString(),
                        name = l.name,
                        fullName = l.name,
                        shortName = l.name,
                        colorId = getBoja(l.eventType.type),
                        color = Color.White,
                        teacher = l.professor,
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
                        description = l.detailDateWithDayName
                    )
                    events.add(event)
                }
                _lessons.postValue(events)
                val testtt = svaFreshPredavanja.toList()
                Log.d("MainViewModel", testtt.toString())
                _permPredavanja.postValue(svaFreshPredavanja.toList())
                Log.d("MainViewModel", permPredavanja.value.toString())
                insertOrUpdateTimeTable(svaFreshPredavanja)
                tableGotPerm.postValue(true)
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
                tableGotPerm.postValue(false)
            }
        }
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
                val tempList = mutableListOf<Predavanja>()
                println("started Fetching Timetable for user")
                val list = timeTableRepository.fetchTimetable(user.username, startDate, endDate)
                list.forEach { println(it.name) }
                for (l in list) {
                    val predavanja = Predavanja()
                    predavanja.objectId = l.id
                    predavanja.id = l.id.toString()
                    predavanja.predavanjeIme = l.eventType.type
                    predavanja.predmetPredavanja = l.name
                    predavanja.rasponVremena = l.timeSpan
                    predavanja.grupa = l.group
                    predavanja.grupaShort = l.group
                    predavanja.dvorana = l.room
                    predavanja.detaljnoVrijeme = l.detailDateWithDayName
                    predavanja.profesor = l.professor

                    tempList.add(predavanja)
                }
                // Removed saving temp classes to db as it should be retained in viewmodel only

                _tempPredavanja.postValue(tempList)
                tableGot.postValue("fetched")
                delay(100)
                tableGot.postValue("fetchedold")
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
                tableGot.postValue("error")
            }
        }
    }

    private suspend fun insertOrUpdateTimeTable(classes: List<Predavanja>) {
        timeTableRepository.insertTimeTable(classes)
    }

}

