package com.tstudioz.fax.fme.feature.studomat.view

import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
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
    private val sharedPreferences: SharedPreferences,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    val isRefreshing = MutableLiveData(false)

    val allYears = MutableLiveData<List<Pair<String, List<StudomatSubject>>>>(emptyList())
    private var loadedTxt = MutableLiveData(StudomatState.UNSET)
    private var student = MutableLiveData(Student())
    var years = MutableLiveData<List<Year>>(emptyList())
    val snackbarHostState: SnackbarHostState = SnackbarHostState()

    val loading = loadedTxt.map { it == StudomatState.FETCHING || it == StudomatState.UNSET }
    val offline
        get() = !networkUtils.isNetworkAvailable()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        viewModelScope.launch(Dispatchers.Main) { snackbarHostState.showSnackbar("Došlo je do pogreške") }
    }

    init {
        //loadData()
        loadAllYears()
        initStudomat()
    }

    /*private fun loadData() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val yearsRealm = repository.readYears().sortedByDescending { it.title }
            val latestYearSubjects = repository.read(yearsRealm.firstOrNull()?.title?.substringBefore(" ") ?: "")
            years.postValue(yearsRealm)
            subjectList.postValue(latestYearSubjects)
            generated.postValue(sharedPreferences.getString("gen" + yearsRealm.firstOrNull()?.title, ""))
        }
    }*/

    private fun loadAllYears() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val yearsRealm = repository.readAll().sortedBy { it.name }.sortedBy { it.semester }.groupBy { it.year }
            allYears.postValue(yearsRealm.toList())
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
                        years.postValue(result.data)
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

    /*fun getChosenYear(year: Year, pulldownTriggered: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) {
                isRefreshing.postValue(true)
            }
            val chosenYear =
                if (year.href.isEmpty() || year.title.isEmpty()) {
                    years.value?.firstOrNull()
                } else {
                    year
                }
            selectedYear.postValue(chosenYear)
            subjectList.postValue(repository.read(year.title.substringBefore(" ")))
            sharedPreferences.getString("gen" + year.title, "").let { generated.postValue(it) }
            if (chosenYear != null) {
                if (networkUtils.isNetworkAvailable()) {
                    when (val result = repository.getYear(chosenYear)) {
                        is StudomatRepositoryResult.ChosenYearResult.Success -> {
                            loadedTxt.postValue(StudomatState.FETCHED)
                            subjectList.postValue(result.data.first)
                            generated.postValue(result.data.second)
                        }

                        is StudomatRepositoryResult.ChosenYearResult.Failure -> {
                            snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                            loadedTxt.postValue(StudomatState.FETCHING_ERROR)
                        }
                    }
                }
            }
            if (pulldownTriggered) {
                isRefreshing.postValue(false)
            }
        }
    }*/

    fun fetchAllYears(freshYears: List<Year> = years.value ?: emptyList(), pulldownTriggered: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) {
                isRefreshing.postValue(true)
            }
            var allYearsTemp = mutableListOf<Pair<String, List<StudomatSubject>>>()
            freshYears.forEach { year ->
                if (networkUtils.isNetworkAvailable()) {
                    when (val result = repository.getYear(year)) {
                        is StudomatRepositoryResult.ChosenYearResult.Success -> {
                            allYearsTemp = allYearsTemp.plus(
                                year.title to result.data.first
                                    .sortedBy { it.name }
                                    .sortedBy { it.semester }
                            ).toMutableList()
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