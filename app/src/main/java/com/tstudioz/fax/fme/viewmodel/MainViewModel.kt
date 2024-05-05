package com.tstudioz.fax.fme.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.EventRealm
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.models.data.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.database.models.TimetableItem
import com.tstudioz.fax.fme.database.models.fromRealmObject
import com.tstudioz.fax.fme.models.data.User
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject
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
    private val dbManager: DatabaseManagerInterface by inject(DatabaseManager::class.java)



    private val _showDay = MutableLiveData<Boolean>().apply { value = false }
    private val _showDayEvent = MutableLiveData<Event>()
    private val _lessons = MutableLiveData<List<Event>>().apply { value = emptyList() }
    private val _periods = MutableLiveData<List<TimeTableInfo>>().apply { value = emptyList() }
    private val _shownWeek = MutableLiveData<LocalDate>().apply { value = LocalDate.now() }
    private val _showWeekChooseMenu = MutableLiveData<Boolean>().apply { value = false }
    val showDay: LiveData<Boolean> = _showDay
    val showDayEvent: LiveData<Event> = _showDayEvent
    val lessons: LiveData<List<Event>> = _lessons
    val periods: LiveData<List<TimeTableInfo>> = _periods
    val shownWeek: LiveData<LocalDate> = _shownWeek
    val showWeekChooseMenu: LiveData<Boolean> = _showWeekChooseMenu

    init {
        fetchTimetableInfo()
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        _lessons.postValue(realm.query<EventRealm>().find().map{ fromRealmObject(it) })
    }
    fun fetchUserTimetable(user: User, startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("started Fetching Timetable for user")
                val events = timeTableRepository.fetchTimetable(user.username, startDate, endDate)
                _shownWeek.postValue(
                    LocalDate.of(
                        startDate.split("-")[2].toInt(),
                        startDate.split("-")[0].toInt(),
                        startDate.split("-")[1].toInt()
                    )
                )
                _lessons.postValue(events)
                insertOrUpdateTimeTable(events)
                tableGotPerm.postValue(true)
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
                tableGotPerm.postValue(false)
            }
        }
    }

    private fun fetchTimetableInfo(
        startDate: String = (LocalDate.now().year - 1).toString() + "-8-1",
        endDate: String = (LocalDate.now().year + 1).toString() + "-8-1"
    ) {
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

    fun showWeekChooseMenu(value: Boolean = true) {
        _showWeekChooseMenu.postValue(value)
    }

    fun showDay(event: Event) {
        _showDayEvent.postValue(event)
        _showDay.postValue(true)
    }

    fun hideDay() {
        _showDay.postValue(false)
    }

    private suspend fun insertOrUpdateTimeTable(classes: List<Event>) {
        timeTableRepository.insertTimeTable(classes)
    }

}

