package com.tstudioz.fax.fme.feature.studomat.view

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.studomat.data.sortedByNameAndSemester
import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYear
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.networking.NetworkUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


class StudomatViewModel(
    private val repository: StudomatRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {
    /**
     * LiveData for refreshing state used for PullToRefresh
     */
    val isRefreshing = MutableLiveData(false)
    val studomatData = MutableLiveData<List<StudomatYear>>(emptyList())
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

    /**
     * Fetches student info and year names and the links for year pages from studomat
     */
    fun getStudomatData(pulldownTriggered: Boolean = false, getSubjects: Boolean = true) {

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

    /**
     * Fetches subjects from each year from studomat
     */
    private fun fetchAllYears(
        freshYears: List<StudomatYearInfo>,
        pulldownTriggered: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) isRefreshing.postValue(true)
            if (networkUtils.isNetworkAvailable()) {
                val allYearsTemp = mutableListOf<StudomatYear>()
                freshYears.map { year ->
                    async {
                        when (val result = repository.getYear(year)) {
                            is StudomatRepositoryResult.ChosenYearResult.Success -> {
                                val populatedYear = StudomatYear(
                                    result.data.first,
                                    result.data.second.sortedByNameAndSemester()
                                )
                                allYearsTemp.add(populatedYear)
                                launch { repository.insert(populatedYear) }
                            }

                            is StudomatRepositoryResult.ChosenYearResult.Failure -> {
                                snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                            }
                        }
                    }
                }.awaitAll()
                val allYearsSorted = allYearsTemp.sortedByDescending { it.yearInfo.academicYear }
                val yearsInfo = allYearsSorted.map { it.yearInfo }
                studomatData.postValue(allYearsSorted)
                yearNames.postValue(yearsInfo)
            }
            if (pulldownTriggered) isRefreshing.postValue(false)
        }
    }

    /**
     * Get JSESSIONID cookie for webview
     */
    fun fetchISVUCookie(): String? = repository.fetchISVUCookie()
}