package com.tstudioz.fax.fme.feature.attendance.view

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.feature.attendance.repository.AttendanceRepositoryInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.networking.NetworkUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AttendanceViewModel(
    private val repository: AttendanceRepositoryInterface,
) : ViewModel() {

    val networkUtils: NetworkUtils by inject(NetworkUtils::class.java)

    private var _error = MutableLiveData(false)
    val error: LiveData<Boolean> = _error

    private var _attendanceList: MutableLiveData<List<List<AttendanceEntry>>> = MutableLiveData(emptyList())
    val attendanceList: LiveData<List<List<AttendanceEntry>>> = _attendanceList

    val snackbarHostState = SnackbarHostState()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Error attendance", exception.toString())
        _error.value = true
    }

    init {
        loadFromDb()
        fetchAttendance()
        _error.observeForever {
            if (it) {
                viewModelScope.launch(context = Dispatchers.IO + handler) {
                    snackbarHostState.showSnackbar("Došlo je do pogreške")
                }
            }
        }
    }

    fun fetchAttendance() {
        viewModelScope.launch(context = Dispatchers.IO + handler) {
            if (networkUtils.isNetworkAvailable()) {
                when (val attendance = repository.fetchAttendance()) {
                    is NetworkServiceResult.AttendanceParseResult.Success -> {
                        val data = attendance.data
                        _attendanceList.postValue(data)
                    }

                    is NetworkServiceResult.AttendanceParseResult.Failure -> {
                        _error.postValue(true)
                    }
                }

            } else {
                snackbarHostState.showSnackbar("Niste povezani")
            }
        }
    }

    private fun loadFromDb() {
        viewModelScope.launch(context = Dispatchers.IO + handler) {
            _attendanceList.postValue(repository.readAttendance())
        }
    }
}


