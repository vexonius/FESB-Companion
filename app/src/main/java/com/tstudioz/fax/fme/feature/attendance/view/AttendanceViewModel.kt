package com.tstudioz.fax.fme.feature.attendance.view

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.feature.attendance.repository.AttendanceRepositoryInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AttendanceViewModel(private val repository: AttendanceRepositoryInterface, private val shPref: SharedPreferences) : ViewModel() {

    var shouldShow = MutableLiveData(true)
    var error = MutableLiveData(false)
        private set
    var attendanceList: MutableLiveData<List<List<Dolazak>>> = MutableLiveData(emptyList())
        private set

    init {
        viewModelScope.launch(context = Dispatchers.IO){
            val att = repository.readAttendance()
            attendanceList.postValue(att)
            shouldShow.postValue(true)
        }
    }

    fun fetchAttendance() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val start = System.currentTimeMillis()
            when (val attendance = repository.fetchAttendance(User(
                shPref.getString("username", "") ?: "",
                shPref.getString("password", "") ?: ""
            ))) {
                is NetworkServiceResult.PrisutnostResult.Success -> {
                    val data = attendance.data as List<List<Dolazak>>
                    repository.insertAttendance(data.flatten())
                    attendanceList.postValue(data)
                    shouldShow.postValue(true)
                }

                is NetworkServiceResult.PrisutnostResult.Failure -> {
                    shouldShow.postValue(false)
                    error.postValue(true)
                }
            }
            val end = System.currentTimeMillis()
            println("Time: ${(end - start)}")
            Log.d("Time", "Time: ${(end - start)}")
        }
    }
}


