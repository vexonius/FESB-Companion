package com.tstudioz.fax.fme.feature.studomat.view

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.studomat.data.sortedByStudomat
import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.networking.NetworkUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StudomatViewModel(
    private val repository: StudomatRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    val isRefreshing = MutableLiveData(false)
    val studomatData = MutableLiveData<List<Pair<StudomatYearInfo, List<StudomatSubject>>>>(emptyList())
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
    private var student = MutableLiveData(Student())
    private var yearNames = MutableLiveData<List<StudomatYearInfo>>(emptyList())

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        viewModelScope.launch(Dispatchers.Main) { snackbarHostState.showSnackbar("Došlo je do pogreške") }
        isRefreshing.postValue(false)
    }

    init {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            studomatData.postValue(repository.readData())
        }
        getStudomatData(getSubjects = false)
    }

    fun getStudomatData(pulldownTriggered: Boolean = false, getSubjects: Boolean = true) {
        repository.loadCookieToWebview()

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (networkUtils.isNetworkAvailable()) {
                if (pulldownTriggered) isRefreshing.postValue(true)
                when (val result = repository.getStudomatDataAndYears()) {
                    is StudomatRepositoryResult.StudentAndYearsResult.Success -> {
                        yearNames.postValue(result.data)
                        student.postValue(result.student)
                        if (getSubjects) fetchAllYears(result.data, pulldownTriggered)
                    }

                    is StudomatRepositoryResult.StudentAndYearsResult.Failure -> {
                        snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    }
                }
            } else {
                snackbarHostState.showSnackbar("Nema interneta")
                return@launch
            }
        }
    }

    private fun fetchAllYears(
        freshYears: List<StudomatYearInfo> = yearNames.value ?: emptyList(),
        pulldownTriggered: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) isRefreshing.postValue(true)
            if (networkUtils.isNetworkAvailable()) {
                val allYearsTemp = mutableListOf<Pair<StudomatYearInfo, List<StudomatSubject>>>()
                freshYears.forEach { year ->
                    when (val result = repository.getYear(year)) {
                        is StudomatRepositoryResult.ChosenYearResult.Success -> {
                            allYearsTemp.add(result.data.first to result.data.second.sortedByStudomat())
                        }

                        is StudomatRepositoryResult.ChosenYearResult.Failure -> {
                            snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                        }
                    }
                }
                val yearsInfo = allYearsTemp.map { it.first }
                studomatData.postValue(allYearsTemp)
                yearNames.postValue(yearsInfo)
            }
            if (pulldownTriggered) isRefreshing.postValue(false)
        }
    }
}