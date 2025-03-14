package com.tstudioz.fax.fme.feature.studomat.view

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private var loadedTxt = MutableLiveData(StudomatState.UNSET)
    private var student = MutableLiveData(Student())
    private var yearNames = MutableLiveData<List<StudomatYearInfo>>(emptyList())

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
            val yearNamesRealm = repository.readYearNames().sortedByDescending { it.year }
            yearNames.postValue(yearNamesRealm)
            val yearsRealmSubjects = repository.read().sortedByStudomat().groupBy { it.year to it.course }

            val groupedData = yearNamesRealm.mapNotNull { yearInfo ->
                yearsRealmSubjects[yearInfo.year to yearInfo.courseName]?.let { subjectsForYearAndCourse ->
                    yearInfo to subjectsForYearAndCourse
                }
            }

            studomatData.postValue(groupedData)
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

    fun fetchAllYears(
        freshYears: List<StudomatYearInfo> = yearNames.value ?: emptyList(),
        pulldownTriggered: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) {
                isRefreshing.postValue(true)
            }
            if (networkUtils.isNetworkAvailable()) {
                val allYearsTemp = mutableListOf<Pair<StudomatYearInfo, List<StudomatSubject>>>()
                val yearInfoTemp = mutableListOf<StudomatYearInfo>()
                freshYears.forEach { year ->
                    when (val result = repository.getYear(year)) {
                        is StudomatRepositoryResult.ChosenYearResult.Success -> {
                            allYearsTemp.add(result.data.first to result.data.second.sortedByStudomat())
                            yearInfoTemp.add(result.data.first)
                        }

                        is StudomatRepositoryResult.ChosenYearResult.Failure -> {
                            snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                        }
                    }
                }
                studomatData.postValue(allYearsTemp)
                yearNames.postValue(yearInfoTemp)
                repository.insertYears(yearInfoTemp)
            }
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