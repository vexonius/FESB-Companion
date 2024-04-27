package com.tstudioz.fax.fme.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.feature.login.repository.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import com.tstudioz.fax.fme.models.data.TimeTableRepositoryInterface
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

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

    fun fetchUserTimetable(user: User, startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("started Fetching Timetable for user")
                val list = timeTableRepository.fetchTimetable(user.username, startDate, endDate)
                val svaFreshPredavanja = mutableListOf<Predavanja>()
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

