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

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AttendanceViewModel(
    private val repository: AttendanceRepositoryInterface,
    private val shPref: SharedPreferences
) : ViewModel() {

    private var _shouldShow = MutableLiveData(true)
    val shouldShow: LiveData<Boolean> = _shouldShow

    private var _error = MutableLiveData(false)
    val error: LiveData<Boolean> = _error

    private var _attendanceList: MutableLiveData<List<List<Dolazak>>> = MutableLiveData(emptyList())
    val attendanceList: LiveData<List<List<Dolazak>>> = _attendanceList

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            _attendanceList.postValue(repository.readAttendance())
            _shouldShow.postValue(true)
        }
    }

    fun fetchAttendance() {
        viewModelScope.launch(context = Dispatchers.IO) {
            when (val attendance = repository.fetchAttendance(
                User(
                    shPref.getString("username", "") ?: "",
                    shPref.getString("password", "") ?: ""
                )
            )) {
                is NetworkServiceResult.PrisutnostResult.Success -> {
                    val data = attendance.data as List<List<Dolazak>>
                    repository.insertAttendance(data.flatten())
                    _attendanceList.postValue(data)
                    _shouldShow.postValue(true)
                }

                is NetworkServiceResult.PrisutnostResult.Failure -> {
                    _shouldShow.postValue(false)
                    _error.postValue(true)
                }
            }
        }
    }
}


