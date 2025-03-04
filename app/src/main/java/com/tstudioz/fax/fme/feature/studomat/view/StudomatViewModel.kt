package com.tstudioz.fax.fme.feature.studomat.view

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.Year
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

    val allYears = MutableLiveData<List<Pair<String, List<StudomatSubject>>>>(emptyList())
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
    private var loadedTxt = MutableLiveData(StudomatState.UNSET)
    private var student = MutableLiveData(Student())
    private var yearNames = MutableLiveData<List<Year>>(emptyList())

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        viewModelScope.launch(Dispatchers.Main) { snackbarHostState.showSnackbar("Došlo je do pogreške") }
    }

    init {
        loadData()
        initStudomat()
    }

    private fun List<StudomatSubject>.sortedByStudomat(): List<StudomatSubject> {
        return this
            .sortedBy { it.name }
            .sortedBy { it.semester }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val yearNamesRealm = repository.readYearNames().sortedByDescending { it.title }
            yearNames.postValue(yearNamesRealm)
            val yearsRealmSubjects = repository.read().sortedByStudomat().groupBy { it.year }
            allYears.postValue(yearsRealmSubjects.toList())
        }
    }

    private fun initStudomat() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (!networkUtils.isNetworkAvailable()) {
                loadedTxt.postValue(StudomatState.FETCHING_ERROR)
                snackbarHostState.showSnackbar("Nema interneta")
                return@launch
            } else {
                when (val result = repository.getStudomatDataAndYears()) {
                    is StudomatRepositoryResult.StudentAndYearsResult.Success -> {
                        fetchAllYears(result.data)
                        yearNames.postValue(result.data)
                        student.postValue(result.student)
                        loadedTxt.postValue(StudomatState.FETCHED)
                    }

                    is StudomatRepositoryResult.StudentAndYearsResult.Failure -> {
                        loadedTxt.postValue(StudomatState.FETCHING_ERROR)
                        snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    }
                }
            }
        }
    }

    fun fetchAllYears(freshYears: List<Year> = yearNames.value ?: emptyList(), pulldownTriggered: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) {
                isRefreshing.postValue(true)
            }
            val allYearsTemp = mutableListOf<Pair<String, List<StudomatSubject>>>()
            freshYears.forEach { year ->
                if (networkUtils.isNetworkAvailable()) {
                    when (val result = repository.getYear(year)) {
                        is StudomatRepositoryResult.ChosenYearResult.Success -> {
                                allYearsTemp.add(year.title to result.data.first.sortedByStudomat())
                        }

                        is StudomatRepositoryResult.ChosenYearResult.Failure -> {
                            snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                        }
                    }
                }
            }
            allYears.postValue(allYearsTemp)
            if (pulldownTriggered) {
                isRefreshing.postValue(false)
            }
        }
    }

    enum class StudomatState {
        UNSET,
        FETCHING,
        FETCHED,
        FETCHING_ERROR
    }
}