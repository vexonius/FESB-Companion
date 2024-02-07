package com.tstudioz.fax.fme.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.models.data.UserRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.UUID

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel(private val repository: UserRepositoryInterface) : ViewModel() {

    val tableGot: MutableLiveData<Boolean> = MutableLiveData()

    var tableGotPerm = MutableLiveData<Boolean>()
    val svaFreshPredavanjaLive = MutableLiveData(mutableListOf<Predavanja>())

    private fun loginUser(user: User) {
        viewModelScope.launch(context = Dispatchers.IO) {
            when (val result = repository.attemptLogin(user.username, user.password)) {
                true -> {

                }
                false -> {

                }
            }
        }
    }

    fun fetchUserTimetable(user: User, startDate: String, endDate: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                println("started Fetching Timetable for user")
                val list = repository.fetchTimetable(user.username, startDate, endDate)
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
                        val str = "${predavanja.id}${predavanja.predavanjeIme}${predavanja.predmetPredavanja}${predavanja.rasponVremena}${predavanja.grupa}${predavanja.dvorana}${predavanja.detaljnoVrijeme}${predavanja.profesor}"
                        val id = UUID.nameUUIDFromBytes(str.toByteArray())
                        predavanja.id = id.toString()

                        svaFreshPredavanja.add(predavanja)
                    }
                svaFreshPredavanjaLive.postValue(svaFreshPredavanja)
                insertOrUpdateTimeTable(svaFreshPredavanja)
                tableGotPerm.postValue(true)
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
                tableGotPerm.postValue(false)
            }
        }
    }

    fun fetchUserTimetableTemp(user: User, startDate: String, endDate: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val svaFreshPredavanja = mutableListOf<Predavanja>()
                println("started Fetching Timetable for user")
                val list = repository.fetchTimetable(user.username, startDate, endDate)
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

                    svaFreshPredavanja.add(predavanja)
                }
                // Removed saving temp classes to db as it should be retained in viewmodel only
                tableGot.postValue(true)
            } catch (e: Exception) {
                Log.e("Error timetable", e.toString())
                tableGot.postValue(false)
            }
        }
    }

    private suspend fun insertOrUpdateTimeTable(classes: List<Predavanja>) {
        repository.insertTimeTable(classes)
    }

}

