package com.tstudioz.fax.fme.ui.mainscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel : ViewModel() {

    private val repository: Repository by inject(Repository::class.java)

    init {
     //   loginUser()
        fetchUserTimetable()
    }

    private fun loginUser() {
        viewModelScope.launch {
            repository.attemptLogin()
                    .onStart { println("Started") }
                    .catch { println("Doslo je do pogreske") }
                    .collect { result -> Timber.d(result.fullname) }
        }
    }

    private fun fetchUserTimetable(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchTimetable("spomenka", "2020-04-06", "2020-04-12")
                    .onStart { println("started Fetching Timetable for user") }
                    .catch { e -> Timber.e(e.toString()) }
                    .collect { list -> list.forEach { println(it.name)} }
        }
    }

}

