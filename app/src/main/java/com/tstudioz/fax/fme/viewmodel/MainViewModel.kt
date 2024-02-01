package com.tstudioz.fax.fme.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.models.data.Repository
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel : ViewModel() {

    private val repository: Repository by inject(Repository::class.java)

    val tableGot: MutableLiveData<Boolean> = MutableLiveData()

    var tableGotPerm = MutableLiveData<Boolean>()
    val svaFreshPredavanjaLive = MutableLiveData(mutableListOf<Predavanja>())


    private fun loginUser(user: User) {
        viewModelScope.launch {
            when (val result = repository.attemptLogin(user)) {
                user -> {
                    Log.d("hello", result.username)
                    println("Started")
                }
                else -> println("Doslo je do pogreske")
            }

        }
    }
    private fun insertOrUpdateTimeTable(freshPredavanja: MutableList<Predavanja>){
        repository.insertOrUpdateTimeTable(freshPredavanja)
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
            }catch (e: Exception){
                Log.e("Error timetable", e.toString())
                tableGotPerm.postValue(false)
            }
        }
    }

    private fun insertTempTimeTable(freshPredavanja: MutableList<Predavanja>){
        repository.insertTempTimeTable(freshPredavanja)
    }
    fun deleteTempTimeTable() {
        repository.deleteTempTimeTable()
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
                insertTempTimeTable(svaFreshPredavanja)
                tableGot.postValue(true)
            }catch (e: Exception){
                Log.e("Error timetable", e.toString())
                tableGot.postValue(false)
            }
        }
    }

}

