package com.tstudioz.fax.fme.feature.attendance.view

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
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
    private val repository: AttendanceRepositoryInterface
) : ViewModel() {

    private val networkUtils: NetworkUtils by inject(NetworkUtils::class.java)

    private var lastFetch = 0L

    private var _attendanceListFull: MutableLiveData<List<List<AttendanceEntry>>> = MutableLiveData(emptyList())
    val attendanceListFull: LiveData<List<List<AttendanceEntry>>> = _attendanceListFull

    private val attendanceFirstSem = _attendanceListFull.map { list -> list.filter { it.firstOrNull()?.semester == 1 } }
    private val attendanceSecondSem = _attendanceListFull.map { list -> list.filter { it.firstOrNull()?.semester == 2 } }

    private val _shownSemester: MutableLiveData<ShownSemester> = MutableLiveData(ShownSemester.BOTH)
    val shownSemester: MutableLiveData<ShownSemester> = _shownSemester

    private val _attendance: LiveData<List<List<AttendanceEntry>>> = _shownSemester.switchMap {
        when (it) {
            ShownSemester.FIRST -> attendanceFirstSem
            ShownSemester.SECOND -> attendanceSecondSem
            ShownSemester.BOTH -> _attendanceListFull
            null -> _attendanceListFull
        }
    }

    val attendance: LiveData<List<List<AttendanceEntry>>> = _attendance

    val snackbarHostState = SnackbarHostState()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Error attendance", exception.toString())
        viewModelScope.launch(Dispatchers.Main) { snackbarHostState.showSnackbar("Došlo je do pogreške") }
    }

    init {
        loadFromDb()
        fetchAttendance()
    }

    private fun has60SecondsPassed(): Boolean = System.currentTimeMillis() - lastFetch > 60000

    fun fetchAttendance() {
        viewModelScope.launch(context = Dispatchers.IO + handler) {
            if (!networkUtils.isNetworkAvailable()) {
                snackbarHostState.showSnackbar("Niste povezani")
                return@launch
            }
            if (!has60SecondsPassed()) {
                return@launch
            }

            lastFetch = System.currentTimeMillis()
            when (val attendance = repository.fetchAttendance()) {
                is NetworkServiceResult.AttendanceParseResult.Success -> {
                    val data = attendance.data
                    _attendanceListFull.postValue(data)
                }

                is NetworkServiceResult.AttendanceParseResult.Failure -> {
                    snackbarHostState.showSnackbar("Došlo je do pogreške")
                }
            }
        }
    }


    private fun loadFromDb() {
        viewModelScope.launch(context = Dispatchers.IO + handler) {
            _attendanceListFull.postValue(repository.readAttendance())
        }
    }

    fun showSemester(semester: ShownSemester) {
        if (_shownSemester.value == semester) _shownSemester.value = ShownSemester.BOTH
        else _shownSemester.value = semester
    }

    enum class ShownSemester {
        FIRST,
        SECOND,
        BOTH
    }
}


