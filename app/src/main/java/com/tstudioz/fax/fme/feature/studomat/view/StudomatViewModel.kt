package com.tstudioz.fax.fme.feature.studomat.view

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.studomat.data.sortedByNameAndSemester
import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYear
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


class StudomatViewModel(
    application: Application,
    private val repository: StudomatRepository,
) : AndroidViewModel(application) {

    val internetAvailable: LiveData<Boolean> = InternetConnectionObserver.get()

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
        viewModelScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(
                getApplication<Application>().applicationContext.getString(
                    R.string.studomat_error_general
                )
            )
        }
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
        if (internetAvailable.value == false) return
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) isRefreshing.postValue(true)
            when (val result = repository.getStudomatDataAndYears()) {
                is StudomatRepositoryResult.StudentAndYearsResult.Success -> {
                    student.postValue(result.student)
                    if (getSubjects) fetchAllYears(result.data, pulldownTriggered)
                }

                is StudomatRepositoryResult.StudentAndYearsResult.Failure -> {
                    snackbarHostState.showSnackbar(getApplication<Application>().applicationContext.getString(R.string.studomar_error))
                }
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
        if (internetAvailable.value == false) return
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) isRefreshing.postValue(true)
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
                            snackbarHostState.showSnackbar(
                                getApplication<Application>().applicationContext.getString(
                                    R.string.studomar_error
                                )
                            )
                        }
                    }
                }
            }.awaitAll()
            val allYearsSorted = allYearsTemp.sortedByDescending { it.yearInfo.academicYear }
            val yearsInfo = allYearsSorted.map { it.yearInfo }
            studomatData.postValue(allYearsSorted)
            yearNames.postValue(yearsInfo)
            if (pulldownTriggered) isRefreshing.postValue(false)
        }
    }
}