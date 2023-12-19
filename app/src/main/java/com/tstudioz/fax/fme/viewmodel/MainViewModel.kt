package com.tstudioz.fax.fme.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.models.data.Repository
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.models.data.TimeTableDao
import com.tstudioz.fax.fme.models.data.TimetableItem
import com.tstudioz.fax.fme.models.data.User
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject



@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel : ViewModel() {

    private val repository: Repository by inject(Repository::class.java)
    val realmLog = Realm.getDefaultInstance()
    val kor = realmLog?.where(Korisnik::class.java)?.findFirst()
    private val user = User(
        kor?.getUsername()
            .toString(), kor?.getUsername()
            .toString(), kor?.getUsername()
            .toString() + "@fesb.hr"
    )

    init {
        //loginUser(user)
        //fetchUserTimetable(User("spomenka","",""), "2023-12-18", "2023-12-25")
    }

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
    fun insertOrUpdateTimeTable(freshPredavanja: MutableList<Predavanja>){
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
                            val predavanja : Predavanja = Predavanja()
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

}

