package com.tstudioz.fax.fme.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.models.data.Repository
import com.tstudioz.fax.fme.database.Korisnik
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
        loginUser(user)
        //fetchUserTimetable()
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

    private fun fetchUserTimetable(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchTimetable("spomenka", "2020-04-06", "2020-04-12")
                    .onStart { println("started Fetching Timetable for user") }
                    .catch { e -> Log.e("Error timetable", e.toString()) }
                    .collect { list -> list.forEach { println(it.name)} }
        }
    }

}

