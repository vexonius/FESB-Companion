package com.tstudioz.fax.fme.feature.attendance.view

import android.content.SharedPreferences
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
class AttendanceViewModel(private val repository: AttendanceRepositoryInterface) : ViewModel() {

    var shouldShow = MutableLiveData(true)
        private set

    private val _attendanceList: MutableLiveData<Map<String, MutableList<Dolazak>>> = MutableLiveData()
    val attendanceList: LiveData<Map<String, MutableList<Dolazak>>> = _attendanceList

    private val shPref: SharedPreferences by inject(SharedPreferences::class.java)

    fun fetchAttendance() {
        viewModelScope.launch(context = Dispatchers.IO) {
            when (val attendance = repository.fetchAttendance(User(
                shPref.getString("username", "") ?: "",
                shPref.getString("password", "") ?: ""
            ))) {
                is NetworkServiceResult.PrisutnostResult.Success -> {
                    val data = attendance.data as Map<String, MutableList<Dolazak>>
                    repository.insertAttendance((data).values.flatten())
                    _attendanceList.postValue(data)
                    shouldShow.postValue(true)
                }

                is NetworkServiceResult.PrisutnostResult.Failure -> {
                    shouldShow.postValue(false)
                }
            }
        }
    }
}


