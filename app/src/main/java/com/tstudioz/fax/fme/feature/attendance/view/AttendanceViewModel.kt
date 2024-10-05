package com.tstudioz.fax.fme.feature.attendance.view

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.feature.attendance.repository.AttendanceRepositoryInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.random.NetworkUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AttendanceViewModel(
    private val repository: AttendanceRepositoryInterface,
) : ViewModel() {

    val networkUtils: NetworkUtils by inject(NetworkUtils::class.java)

    private var _shouldShow = MutableLiveData(true)
    val shouldShow: LiveData<Boolean> = _shouldShow

    private var _error = MutableLiveData(false)
    val error: LiveData<Boolean> = _error

    private var _attendanceList: MutableLiveData<List<List<AttendanceEntry>>> =
        MutableLiveData(emptyList())
    val attendanceList: LiveData<List<List<AttendanceEntry>>> = _attendanceList

    val snackbarHostState = SnackbarHostState()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Error timetable", exception.toString())
        _shouldShow.value = false
        _error.value = true
    }

    init {
        loadFromDb()
        fetchAttendance()
        _error.observeForever {
            if (it) {
                viewModelScope.launch { snackbarHostState.showSnackbar("Došlo je do pogreške") }
            }
        }
    }

    fun fetchAttendance() {
        if (networkUtils.isNetworkAvailable()) {
            viewModelScope.launch(context = Dispatchers.IO + handler) {
                when (val attendance = repository.fetchAttendance()) {
                    is NetworkServiceResult.AttendanceParseResult.Success -> {
                        val data = attendance.data
                        _attendanceList.postValue(data)
                        _shouldShow.postValue(true)
                    }

                    is NetworkServiceResult.AttendanceParseResult.Failure -> {
                        _error.postValue(true)
                    }
                }
            }
        } else {
            viewModelScope.launch { snackbarHostState.showSnackbar("Niste povezani") }
        }
    }

    private fun loadFromDb() {
        viewModelScope.launch(context = Dispatchers.IO + handler) {
            _attendanceList.postValue(repository.readAttendance())
            _shouldShow.postValue(true)
        }
    }
}


