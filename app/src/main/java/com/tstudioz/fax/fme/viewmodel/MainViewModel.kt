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



@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel : ViewModel() {

    private val repository: Repository by inject(Repository::class.java)

    private var _tableGot = MutableLiveData<Boolean>()
    val tableGot: LiveData<Boolean>
        get() = _tableGot
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
                if(list.isEmpty()) {
                    println("List is empty")
                }
                else {
                    list.forEach { println(it.name) }
                    val svaFreshPredavanja = mutableListOf<Predavanja>()
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
                        insertOrUpdateTimeTable(svaFreshPredavanja)
                }
            }catch (e: Exception){
                Log.e("Error timetable", e.toString())
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
                println("started Fetching Timetable for user")
                val list = repository.fetchTimetable(user.username, startDate, endDate)
                if(list.isEmpty()) {
                    println("List is empty")
                }
                else {
                    list.forEach { println(it.name) }
                    val svaFreshPredavanja = mutableListOf<Predavanja>()
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
                    _tableGot.postValue(true)
                }
            }catch (e: Exception){
                Log.e("Error timetable", e.toString())
                _tableGot.postValue(false)
            }
        }
    }

}

